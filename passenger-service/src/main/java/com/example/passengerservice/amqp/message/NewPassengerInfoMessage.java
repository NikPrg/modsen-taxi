package com.example.passengerservice.amqp.message;

import lombok.Builder;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.kafka.support.KafkaHeaders;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
@Builder
public record NewPassengerInfoMessage(
        UUID passengerExternalId

) implements Serializable {
}
