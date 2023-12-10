package com.example.passengerservice.amqp.message;

import java.io.Serializable;
import java.util.UUID;

public record ChangeDefaultPaymentMethodMessage(
        UUID passengerExternalId

) implements Serializable {
}
