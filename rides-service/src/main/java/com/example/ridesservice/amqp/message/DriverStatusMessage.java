package com.example.ridesservice.amqp.message;

import com.example.ridesservice.model.enums.DriverStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;
@Builder
public record DriverStatusMessage(
        UUID driverExternalId,
        DriverStatus driverStatus

) implements Serializable {
}
