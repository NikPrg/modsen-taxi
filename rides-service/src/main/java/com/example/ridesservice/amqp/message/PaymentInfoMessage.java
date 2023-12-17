package com.example.ridesservice.amqp.message;

import com.example.ridesservice.model.enums.PaymentStatus;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PaymentInfoMessage(
        UUID cardExternalId,
        UUID rideExternalId,
        PaymentStatus paymentStatus

) implements Serializable {
}