package com.example.paymentservice.amqp.message;

import java.io.Serializable;
import java.util.UUID;

public record CardInfoMessage(
        UUID cardExternalId

) implements Serializable {
}
