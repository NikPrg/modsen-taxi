package com.example.paymentservice.amqp.message;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record RidePaymentMessage(
        UUID cardExternalId,
        UUID rideExternalId,
        BigDecimal rideCost

) implements Serializable {
}
