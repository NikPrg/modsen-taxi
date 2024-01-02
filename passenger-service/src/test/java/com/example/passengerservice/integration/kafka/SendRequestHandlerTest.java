package com.example.passengerservice.integration.kafka;

import com.example.passengerservice.amqp.channelGateway.KafkaChannelGateway;
import com.example.passengerservice.amqp.handler.SendRequestHandler;
import com.example.passengerservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.amqp.message.RemovePassengerInfoMessage;
import com.example.passengerservice.integration.TestcontainersBase;
import com.example.passengerservice.integration.kafka.config.KafkaConsumerConfigTest;
import com.example.passengerservice.util.DataUtil;
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

    @Value("${app.kafka.topic.card-default-status-details}")
    private String cardDefaultStatusDetailsTopic;

    @Value("${app.kafka.topic.new-passenger-details}")
    private String newPassengerDetailsTopic;

    @Value("${app.kafka.topic.removed-passenger-details}")
    private String removedPassengerDetailsTopic;

    @Test
    void sendDefaultCardChangeRequest_shouldSendToCardStatusDetailsTopic() {
        Consumer<String, ChangeCardUsedAsDefaultMessage> consumer = consumerConfig
                .setUpConsumer(cardDefaultStatusDetailsTopic, ChangeCardUsedAsDefaultMessage.class);
        ChangeCardUsedAsDefaultMessage request = DataUtil.defaultChangeCardUsedAsDefaultMessage();

        sendRequestHandler.sendDefaultCardChangeRequest(request);
        ConsumerRecords<String, ChangeCardUsedAsDefaultMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(request));
    }

    @Test
    void sendNewPassengerToKafka_shouldSendToNewPassengerDetailsTopic() {
        Consumer<String, NewPassengerInfoMessage> consumer = consumerConfig.setUpConsumer(newPassengerDetailsTopic, NewPassengerInfoMessage.class);
        NewPassengerInfoMessage request = DataUtil.defaultNewPassengerMessage();

        sendRequestHandler.sendNewPassengerToKafka(request);
        ConsumerRecords<String, NewPassengerInfoMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(request));
    }

    @Test
    void sendPassengerRemovalToKafka_shouldSendToRemovedPassengerDetailsTopic() {
        Consumer<String, RemovePassengerInfoMessage> consumer = consumerConfig.setUpConsumer(removedPassengerDetailsTopic, RemovePassengerInfoMessage.class);
        RemovePassengerInfoMessage request = DataUtil.defaultRemovePassengerInfoMessage();

        sendRequestHandler.sendPassengerRemovalToKafka(request);
        ConsumerRecords<String, RemovePassengerInfoMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(request));
    }
}
