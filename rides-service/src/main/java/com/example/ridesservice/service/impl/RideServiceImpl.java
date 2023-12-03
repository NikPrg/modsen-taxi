package com.example.ridesservice.service.impl;

import com.example.ridesservice.amqp.channelGateway.KafkaChannelGateway;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.PaymentInfoResponse;
import com.example.ridesservice.dto.response.ride.*;
import com.example.ridesservice.exception.DriverAlreadyInUseException;
import com.example.ridesservice.exception.PassengerRideNotFoundException;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.DriverStatus;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.model.projection.RideView;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.util.BuildFactory;
import com.example.ridesservice.util.RideVerifier;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.ridesservice.util.ExceptionMessagesConstants.*;


@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepo;
    private final DriverInfoRepository driverInfoRepo;
    private final RideMapper rideMapper;
    private final WebClient webClient;
    private final KafkaChannelGateway kafkaChannelGateway;
    private final RideVerifier rideVerifier;
    private final BuildFactory buildFactory;
    @Value("${app.routes.passengers.get-payment-method}")
    private String passengersGetPaymentMethodUri;
    @Value("${app.api.web-client.max-retry-attempts}")
    private int maxRetryAttempts;
    @Value("${app.api.web-client.delay-in-ms}")
    private int delayMillis;
    @Value("${app.config.base-cost-per-km}")
    private int baseCostPerKm;

    @Override
    public GetRideResponse findRideByPassengerExternalId(UUID passengerExternalId, UUID rideExternalId) {
        Ride ride = rideRepo.findByExternalIdAndPassengerExternalId(rideExternalId, passengerExternalId)
                .orElseThrow(() -> new PassengerRideNotFoundException(PASSENGER_RIDE_NOT_FOUND_EXCEPTION_MESSAGE
                        .formatted(rideExternalId, passengerExternalId)));

        return rideMapper.toGetRideDto(ride);
    }

    @Transactional(readOnly = true)
    @Override
    public AllRidesResponse findAllPassengerRides(UUID passengerExternalId, Pageable pageable) {
        Page<RideView> allPassengerRidesViews = rideRepo.findByPassengerExternalId(passengerExternalId, pageable);
        return buildFactory.buildAllRidesDto(allPassengerRidesViews);
    }

    @Transactional
    @Override
    public CreateRideResponse bookRide(UUID passengerExternalId, CreateRideRequest createRideDto) {
        var rideCost = calculateRideCost(createRideDto);
        var ride = rideMapper.toRide(createRideDto, passengerExternalId, rideCost);

        kafkaChannelGateway.sendRideInfoRequestToKafka(
                new GenericMessage<>(buildFactory.buildRideInfoMessage(ride)));

        rideRepo.save(ride);

        return rideMapper.toCreateRideDto(ride);
    }

    @Transactional
    @Override
    public AcceptRideResponse acceptRide(UUID driverExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));
        rideVerifier.verifyAcceptPossibility(ride);

        var driver = driverInfoRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        if (ObjectUtils.notEqual(DriverStatus.AVAILABLE, driver.getDriverStatus())) {
            throw new DriverAlreadyInUseException(DRIVER_ALREADY_IN_USE_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }

        rideMapper.updateRideOnAcceptance(driver, ride);

        driver.addRide(ride);
        driver.setDriverStatus(DriverStatus.TOWARDS_PASSENGER);

        return rideMapper.toAcceptRideDto(ride);
    }

    @Transactional
    @Override
    public StartRideResponse startRide(UUID driverExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));
        rideVerifier.verifyStartPossibility(ride, driverExternalId);

        initiateRideStartActions(ride);

        kafkaChannelGateway.sendDriverStatusRequestToKafka(
                new GenericMessage<>(buildFactory.buildDriverStatusMessage(ride.getDriver())));

        return rideMapper.toStartRideDto(ride);
    }

    @Transactional
    @Override
    public FinishRideResponse finishRide(UUID driverExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));
        rideVerifier.verifyFinishPossibility(ride, driverExternalId);

        PaymentInfoResponse paymentInfo = requestPassengerPaymentInfo(ride.getPassengerExternalId());

        //async call to payment microservice to provide payment for the ride

        rideMapper.updateRideOnFinish(Objects.requireNonNull(paymentInfo).paymentMethod(), ride);

        var driver = ride.getDriver();
        driver.setDriverStatus(DriverStatus.AVAILABLE);

        kafkaChannelGateway.sendDriverStatusRequestToKafka(
                new GenericMessage<>(buildFactory.buildDriverStatusMessage(driver)));

        return rideMapper.toFinishRideResponse(ride);
    }

    private PaymentInfoResponse requestPassengerPaymentInfo(UUID passengerExternalId) {
        return webClient.get()
                .uri(passengersGetPaymentMethodUri, passengerExternalId)
                .exchangeToMono(this::getPaymentInfoResponse)
                .retryWhen(Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(delayMillis)))
                .block();
    }

    private Mono<PaymentInfoResponse> getPaymentInfoResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(PaymentInfoResponse.class);
        }
        return Mono.error(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private double calculateRideCost(CreateRideRequest createRideRequestDto) {

        var pickUpAddress = createRideRequestDto.pickUpAddress();
        var destinationAddress = createRideRequestDto.destinationAddress();
        var totalDistance = getTotalDistance(pickUpAddress, destinationAddress);

        return Math.round(totalDistance * baseCostPerKm);
    }

    private double getTotalDistance(String pickUpAddress, String destinationAddress) {
        return pickUpAddress.concat(destinationAddress).length() * Math.random() * 3;
    }

    private void initiateRideStartActions(Ride ride) {
        ride.setRideStartedAt(LocalTime.now());
        ride.setRideStatus(RideStatus.STARTED);
        ride.getDriver().setDriverStatus(DriverStatus.UNAVAILABLE);
    }
}
