package com.example.cardservice.amqp.message;

import java.io.Serializable;
import java.util.UUID;

public record ErrorInfoMessage(
        UUID passengerExternalId,
        String exceptionMessage

) implements Serializable {
}
