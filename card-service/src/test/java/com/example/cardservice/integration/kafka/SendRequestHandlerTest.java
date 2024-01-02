package com.example.cardservice.integration.kafka;

import com.example.cardservice.amqp.handler.SendRequestHandler;
import com.example.cardservice.amqp.message.CardInfoMessage;
import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.cardservice.amqp.message.ErrorInfoMessage;
import com.example.cardservice.integration.TestcontainersBase;
import com.example.cardservice.integration.kafka.config.KafkaConsumerConfigTest;
import com.example.cardservice.util.DataUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SendRequestHandlerTest extends TestcontainersBase {

    @Autowired
    private SendRequestHandler sendRequestHandler;

    @Autowired
    private KafkaConsumerConfigTest consumerConfig;

    @Value("${app.kafka.topic.payment-method-details}")
    private String paymentMethodDetailsTopic;

    @Value("${app.kafka.topic.new-card-details}")
    private String newCardDetailsTopic;

    @Value("${app.kafka.topic.error-card-details}")
    private String errorCardDetailsTopic;

    @Test
    void sendDefaultPaymentMethodChangeRequestToKafka_shouldSendToPaymentMethodDetailsTopic() {
        Consumer<String, ChangeDefaultPaymentMethodMessage> consumer = consumerConfig.setUpConsumer(paymentMethodDetailsTopic, ChangeDefaultPaymentMethodMessage.class);
        ChangeDefaultPaymentMethodMessage message = DataUtil.defaultChangeDefaultPaymentMethodMessage();

        sendRequestHandler.sendDefaultPaymentMethodChangeRequestToKafka(message);
        ConsumerRecords<String, ChangeDefaultPaymentMethodMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(message));
    }

    @Test
    void sendNewCardInfoToKafka_shouldSendToNewCardDetailsTopic() {
        Consumer<String, CardInfoMessage> consumer = consumerConfig.setUpConsumer(newCardDetailsTopic, CardInfoMessage.class);
        CardInfoMessage message = DataUtil.defaultCardInfoMessage();

        sendRequestHandler.sendNewCardInfoToKafka(message);
        ConsumerRecords<String, CardInfoMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(message));
    }

    @Test
    void sendErrorInfoMessageToKafka_shouldSendToErrorCardDetailsTopic() {
        Consumer<String, ErrorInfoMessage> consumer = consumerConfig.setUpConsumer(errorCardDetailsTopic, ErrorInfoMessage.class);
        ErrorInfoMessage message = DataUtil.defaultErrorInfoMessage();

        sendRequestHandler.sendErrorInfoMessageToKafka(message);
        ConsumerRecords<String, ErrorInfoMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(message));
    }
}
