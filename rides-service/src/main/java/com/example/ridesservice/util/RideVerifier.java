package com.example.ridesservice.util;

import com.example.ridesservice.exception.*;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.RideStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.ridesservice.util.ExceptionMessagesConstants.*;

@Component
public class RideVerifier {
    public void verifyAcceptPossibility(Ride ride) {
        if (ObjectUtils.isNotEmpty(ride.getDriver())) {
            throw new DriverNotBelongRideException(RIDE_BELONG_ANOTHER_DRIVER_EXCEPTION_MESSAGE.formatted(ride.getExternalId()));
        }
    }

    public void verifyStartPossibility(Ride ride, UUID driverExternalId) {
        UUID rideExternalId = ride.getExternalId();

        if (ObjectUtils.isEmpty(ride.getDriver())) {
            throw new RideNotAcceptedException(RIDE_NOT_ACCEPTED_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }

        if (ObjectUtils.notEqual(ride.getDriver().getExternalId(), driverExternalId)) {
            throw new DriverNotBelongRideException(RIDE_BELONG_ANOTHER_DRIVER_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }

        if (RideStatus.STARTED.equals(ride.getRideStatus())) {
            throw new RideAlreadyStartedExceptionMessage(RIDE_ALREADY_STARTED_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }

        if (RideStatus.FINISHED.equals(ride.getRideStatus())) {
            throw new RideAlreadyFinishedExceptionMessage(RIDE_ALREADY_FINISHED_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }
    }

    public void verifyFinishPossibility(Ride ride, UUID driverExternalId) {
        UUID rideExternalId = ride.getExternalId();

        if (ObjectUtils.isEmpty(ride.getDriver())) {
            throw new RideNotAcceptedException(RIDE_NOT_ACCEPTED_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }

        if (ObjectUtils.notEqual(ride.getDriver().getExternalId(), driverExternalId)) {
            throw new DriverNotBelongRideException(RIDE_BELONG_ANOTHER_DRIVER_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }

        if (RideStatus.ACCEPTED.equals(ride.getRideStatus())) {
            throw new RideNotStartedExceptionMessage(RIDE_NOT_STARTED_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }

        if (RideStatus.FINISHED.equals(ride.getRideStatus())) {
            throw new RideAlreadyFinishedExceptionMessage(RIDE_ALREADY_FINISHED_EXCEPTION_MESSAGE.formatted(rideExternalId));
        }
    }
}
