package com.example.driverservice.amqp.message;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record RideInfoMessage(
        UUID externalId,
        String pickUpAddress,
        String destinationAddress,
        double cost

) implements Serializable {
}
