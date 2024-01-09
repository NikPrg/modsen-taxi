package com.example.paymentservice.integration.kafka;

import com.example.paymentservice.amqp.message.CardInfoMessage;
import com.example.paymentservice.integration.TestcontainersBase;
import com.example.paymentservice.repository.CardBalanceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaConsumerIntegrationTest extends TestcontainersBase {

    @Autowired
    private CardBalanceRepository cardBalanceRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.consumeCardInfo-in-0.destination}")
    private String newCardDetailsTopic;

    @Test
    void addNewCard_shouldCreateNewCard() {
        CardInfoMessage message = new CardInfoMessage(UUID.randomUUID());
        UUID cardExternalId = message.cardExternalId();

        kafkaTemplate.send(newCardDetailsTopic, message);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    var actual = cardBalanceRepo.findByCardExternalId(cardExternalId);
                    assertThat(actual).isPresent();
                });
    }
}
