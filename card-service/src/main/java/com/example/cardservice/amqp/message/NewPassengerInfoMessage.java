package com.example.cardservice.amqp.message;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record NewPassengerInfoMessage(
        UUID passengerExternalId

) implements Serializable {
}
