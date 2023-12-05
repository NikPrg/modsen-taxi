package com.example.ridesservice.amqp.message;

import com.example.ridesservice.model.enums.DriverStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record DriverInfoMessage(
        UUID externalId,
        String firstName,
        String lastName,
        DriverStatus driverStatus,
        CarInfoMessage carInfoMessage

) implements Serializable {
}
