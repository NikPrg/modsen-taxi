package com.example.passengerservice.integration.kafka;

import com.example.passengerservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.passengerservice.integration.TestcontainersBase;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.enums.PaymentMethod;
import com.example.passengerservice.repository.PassengerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.example.passengerservice.util.EntitiesUtil.SAVELIY_EXTERNAL_ID;
import static com.example.passengerservice.util.EntitiesUtil.saveliyBennett;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaConsumerIntegrationTest extends TestcontainersBase {

    @Autowired
    private PassengerRepository passengerRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.payment-method-details}")
    private String paymentMethodDetailsTopic;

    @Test
    void updateDefaultPaymentMethod_shouldUpdatePaymentMethod() {
        // arrange
        Passenger passenger = saveliyBennett();
        passengerRepo.saveAndFlush(passenger);
        ChangeDefaultPaymentMethodMessage message = new ChangeDefaultPaymentMethodMessage(SAVELIY_EXTERNAL_ID);

        // act
        kafkaTemplate.send(paymentMethodDetailsTopic, message);

        // assert
        await()
                .pollInterval(Duration.ofSeconds(5))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Passenger actual = passengerRepo.findByExternalId(message.passengerExternalId()).get();
                    assertThat(actual.getDefaultPaymentMethod()).isEqualTo(PaymentMethod.CASH);
                });
    }
}
