package com.example.ridesservice.service.impl;

import com.example.ridesservice.amqp.handler.SendRequestHandler;
import com.example.ridesservice.amqp.message.PaymentInfoMessage;
import com.example.ridesservice.dto.request.CreateRideRequest;
import com.example.ridesservice.dto.response.ride.*;
import com.example.ridesservice.exception.DriverAlreadyInUseException;
import com.example.ridesservice.exception.PassengerRideNotFoundException;
import com.example.ridesservice.feign.client.CardClient;
import com.example.ridesservice.feign.client.PassengerClient;
import com.example.ridesservice.feign.response.DefaultCardResponse;
import com.example.ridesservice.feign.response.PaymentMethodResponse;
import com.example.ridesservice.mapper.RideMapper;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.DriverStatus;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.PaymentStatus;
import com.example.ridesservice.model.enums.RideStatus;
import com.example.ridesservice.model.projection.RideView;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.repository.RideRepository;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.util.RideVerifier;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

import static com.example.ridesservice.util.DataComposerUtils.*;
import static com.example.ridesservice.util.ExceptionMessagesConstants.*;
import static java.time.temporal.ChronoUnit.MINUTES;


@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepo;
    private final DriverInfoRepository driverInfoRepo;
    private final RideMapper rideMapper;
    private final SendRequestHandler sendRequestHandler;
    private final PassengerClient passengerClient;
    private final CardClient cardClient;
    private final RideVerifier rideVerifier;

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

        sendRequestHandler.sendRideInfoRequestToKafka(buildRideInfoMessage(ride));

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

        sendRequestHandler.sendDriverStatusRequestToKafka(buildDriverStatusMessage(ride.getDriver()));
        return rideMapper.toStartRideDto(ride);
    }

    @Transactional
    @Override
    public FinishRideResponse finishRide(UUID driverExternalId, UUID rideExternalId) {
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() -> new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));
        rideVerifier.verifyFinishPossibility(ride, driverExternalId);

        providePassengerPayment(ride);
        updateRideAndDriverOnFinish(ride);

        sendRequestHandler.sendDriverStatusRequestToKafka(buildDriverStatusMessage(ride.getDriver()));
        return rideMapper.toFinishRideResponse(ride);
    }

    @Transactional
    @Override
    public void handlePaymentResult(PaymentInfoMessage message) {
        UUID rideExternalId = message.rideExternalId();
        var ride = rideRepo.findByExternalId(rideExternalId)
                .orElseThrow(() ->
                        new EntityNotFoundException(RIDE_NOT_FOUND_EXCEPTION_MESSAGE.formatted(rideExternalId)));

        ride.setPaymentMethod(PaymentMethod.CARD);
        ride.setPaymentStatus(message.paymentStatus());
        rideRepo.save(ride);
    }

    private void updateRideAndDriverOnFinish(Ride ride) {
        ride.setRideStatus(RideStatus.FINISHED);
        ride.setRideDuration(MINUTES.between(ride.getRideStartedAt(), LocalTime.now()));
        ride.getDriver().setDriverStatus(DriverStatus.AVAILABLE);
    }

    private void providePassengerPayment(Ride ride) {
        UUID passengerExternalId = ride.getPassengerExternalId();
        PaymentMethodResponse passengerPaymentMethod = passengerClient.findPassengerPaymentMethod(passengerExternalId);

        if (PaymentMethod.CARD.equals(passengerPaymentMethod.paymentMethod())) {
            DefaultCardResponse defaultCard = cardClient.findDefaultCardByPassengerExternalId(passengerExternalId);
            sendRequestHandler.sendRidePaymentRequestToKafka(buildRidePaymentMessage(defaultCard, ride));
        } else {
            ride.setPaymentMethod(PaymentMethod.CASH);
            ride.setPaymentStatus(PaymentStatus.PAID);
        }
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
