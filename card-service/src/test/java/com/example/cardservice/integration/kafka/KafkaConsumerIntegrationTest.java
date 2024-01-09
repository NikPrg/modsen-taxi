package com.example.cardservice.integration.kafka;

import com.example.cardservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.cardservice.amqp.message.NewPassengerInfoMessage;
import com.example.cardservice.integration.TestcontainersBase;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerInfo;
import com.example.cardservice.model.enums.PaymentMethod;
import com.example.cardservice.repository.CardRepository;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.util.DataUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaConsumerIntegrationTest extends TestcontainersBase {

    @Autowired
    private PassengerInfoRepository passengerInfoRepo;

    @Autowired
    private CardRepository cardRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.card-default-status-details}")
    private String cardDefaultStatusDetailsTopic;

    @Value("${app.kafka.topic.new-passenger-details}")
    private String newPassengerDetailsTopic;

    @Test
    void setCardAsUsedDefault_shouldMarkCardAsUsedDefault() {
        // arrange
        PassengerInfo passengerInfo = DataUtil.defaultPassengerInfoWithCard();
        Card card = DataUtil.defaultCard();
        cardRepo.save(card);
        passengerInfoRepo.save(passengerInfo);
        var message = new ChangeCardUsedAsDefaultMessage(passengerInfo.getExternalId(), card.getExternalId(), PaymentMethod.CARD);

        // act
        kafkaTemplate.send(cardDefaultStatusDetailsTopic, message);

        // assert
        await()
                .pollInterval(Duration.ofSeconds(5))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    PassengerInfo actual = passengerInfoRepo
                            .findByExternalIdAndCardExternalId(message.passengerExternalId(), message.cardExternalId()).get();
                    assertThat(extractUsedAsDefault(actual)).isEqualTo(TRUE);
                });
    }

    @Test
    void removeCardAsUsedDefault_shouldUnsetCardAsDefaultUsed() {
        // arrange
        PassengerInfo passengerInfo = DataUtil.initPassengerInfo();
        Card card = DataUtil.defaultCard();
        cardRepo.save(card);
        passengerInfo.addCard(card);
        passengerInfo.getCards().get(0).setUsedAsDefault(true);
        passengerInfoRepo.save(passengerInfo);
        var message = new ChangeCardUsedAsDefaultMessage(passengerInfo.getExternalId(), card.getExternalId(), PaymentMethod.CASH);

        // act
        kafkaTemplate.send(cardDefaultStatusDetailsTopic, message);

        // assert
        await()
                .pollInterval(Duration.ofSeconds(5))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    PassengerInfo actual = passengerInfoRepo
                            .findByExternalIdAndCardExternalId(message.passengerExternalId(), message.cardExternalId()).get();
                    assertThat(extractUsedAsDefault(actual)).isEqualTo(FALSE);
                });
    }

    @Test
    void saveNewPassengerInfo_shouldSaveNewData() {
        // arrange
        UUID passengerExternalId = UUID.randomUUID();
        var message = new NewPassengerInfoMessage(passengerExternalId);

        // act
        kafkaTemplate.send(newPassengerDetailsTopic, message);

        // assert
        await()
                .pollInterval(Duration.ofSeconds(5))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var actual = passengerInfoRepo.findByExternalId(message.passengerExternalId());
                    assertThat(actual).isPresent();
                });
    }

    private static boolean extractUsedAsDefault(PassengerInfo actual) {
        return actual.getCards().get(0).isUsedAsDefault();
    }
}
