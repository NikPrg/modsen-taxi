package com.example.ridesservice.service.impl;

import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.PaymentInfoResponse;
import com.example.ridesservice.dto.response.ride.*;
import com.example.ridesservice.exception.DriverAlreadyInUseException;
import com.example.ridesservice.exception.PassengerRideNotFoundException;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.DriverStatus;
import com.example.ridesservice.model.projection.RideView;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.util.RideVerifier;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
    private final RideVerifier rideVerifier;
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
        return buildAllRidesDto(allPassengerRidesViews);
    }

    @Transactional
    @Override
    public CreateRideResponse bookRide(UUID passengerExternalId, CreateRideRequest createRideDto) {
        var rideCost = calculateRideCost(createRideDto);
        var ride = rideMapper.toRide(createRideDto, passengerExternalId, rideCost);

        //notification drivers via Kafka

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

        if (DriverStatus.UNAVAILABLE.equals(driver.getDriverStatus())) {
            throw new DriverAlreadyInUseException(DRIVER_ALREADY_IN_USE_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }

        rideMapper.updateRideOnAcceptance(driver, ride);

        driver.addRide(ride);
        driver.setDriverStatus(DriverStatus.UNAVAILABLE);

        //change driverStatus in drivers-service via Kafka

        return rideMapper.toAcceptRideDto(ride);
    }

    @Transactional
    @Override
    public StartRideResponse startRide(UUID driverExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));
        rideVerifier.verifyStartPossibility(ride, driverExternalId);

        var updatedRide = rideMapper.updateRideOnStarted(ride);

        rideRepo.save(updatedRide);

        //change driverStatus in drivers-service via Kafka

        return rideMapper.toStartRideDto(ride);
    }

    @Transactional
    @Override
    public FinishRideResponse finishRide(UUID driverExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));
        rideVerifier.verifyFinishPossibility(ride, driverExternalId);

        UUID passengerExternalId = ride.getPassengerExternalId();

        PaymentInfoResponse paymentInfo = webClient.get()
                .uri(passengersGetPaymentMethodUri, passengerExternalId)
                .exchangeToMono(this::getPaymentInfoResponse)
                .retryWhen(Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(delayMillis)))
                .block();

        //async call to payment microservice to provide payment for the ride

        rideMapper.updateRideOnFinish(Objects.requireNonNull(paymentInfo).paymentMethod(), ride);

        ride.getDriver().setDriverStatus(DriverStatus.AVAILABLE);

        //change driverStatus in drivers-service via Kafka

        return rideMapper.toFinishRideResponse(ride);
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

    private AllRidesResponse buildAllRidesDto(Page<RideView> allPassengerRidesViews) {
        return AllRidesResponse.builder()
                .rideViewList(allPassengerRidesViews.getContent())
                .currentPageNumber(allPassengerRidesViews.getNumber())
                .totalPages(allPassengerRidesViews.getTotalPages())
                .totalElements(allPassengerRidesViews.getTotalElements())
                .build();
    }
}
