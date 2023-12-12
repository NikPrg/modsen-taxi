package com.example.passengerservice.amqp.message;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record RemovePassengerInfoMessage(
        UUID passengerExternalId

) implements Serializable {
}
