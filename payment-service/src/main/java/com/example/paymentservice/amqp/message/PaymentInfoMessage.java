package com.example.paymentservice.amqp.message;

import com.example.paymentservice.model.enums.PaymentStatus;
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
