package com.example.driverservice.amqp.message;

import com.example.driverservice.model.enums.DriverStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;
@Builder
public record DriverInfoMessage(
        UUID externalId,
        String firstName,
        String lastName,
        DriverStatus driverStatus
) implements Serializable {
}
