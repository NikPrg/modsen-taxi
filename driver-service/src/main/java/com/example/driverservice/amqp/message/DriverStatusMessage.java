package com.example.driverservice.amqp.message;

import com.example.driverservice.model.enums.DriverStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record DriverStatusMessage(
        UUID driverExternalId,
        DriverStatus driverStatus

) implements Serializable {
}
