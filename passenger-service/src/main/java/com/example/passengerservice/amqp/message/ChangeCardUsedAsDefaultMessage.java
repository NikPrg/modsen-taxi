package com.example.passengerservice.amqp.message;

import com.example.passengerservice.model.PaymentMethod;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;
@Builder
public record ChangeCardUsedAsDefaultMessage(
        UUID passengerExternalId,
        @Nullable
        UUID cardExternalId,
        PaymentMethod paymentMethod

) implements Serializable {
}
