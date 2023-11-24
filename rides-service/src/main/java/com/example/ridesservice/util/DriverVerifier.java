package com.example.ridesservice.util;

import com.example.ridesservice.exception.DriverAlreadyInUseException;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.enums.DriverStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.example.ridesservice.util.ExceptionMessagesConstants.*;

@Component
public class DriverVerifier {
    public void verifyAcceptPossibility(DriverInfo driverInfo) {
        UUID driverExternalId = driverInfo.getExternalId();

        if (DriverStatus.UNAVAILABLE.equals(driverInfo.getDriverStatus())) {
            throw new DriverAlreadyInUseException(DRIVER_ALREADY_IN_USE_EXCEPTION_MESSAGE.formatted(driverExternalId));
        }
    }
}
