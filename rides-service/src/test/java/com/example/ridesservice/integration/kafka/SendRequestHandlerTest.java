package com.example.ridesservice.integration.kafka;

import com.example.ridesservice.amqp.handler.SendRequestHandler;
import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.amqp.message.RidePaymentMessage;
import com.example.ridesservice.integration.TestcontainersBase;
import com.example.ridesservice.integration.kafka.config.KafkaConsumerConfigTest;
import com.example.ridesservice.util.DataUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SendRequestHandlerTest extends TestcontainersBase {

    @Autowired
    private SendRequestHandler sendRequestHandler;

    @Autowired
    private KafkaConsumerConfigTest consumerConfig;

    @Value("${app.kafka.topic.ride-info-events}")
    private String rideInfoEventsTopic;

    @Value("${app.kafka.topic.driver-status-events}")
    private String driverStatusEventsTopic;

    @Value("${app.kafka.topic.ride-payment-events}")
    private String ridePaymentEventsTopic;

    @Test
    void sendRideInfoRequestToKafka_shouldSendToRideInfoEventsTopic() {
        Consumer<String, RideInfoMessage> consumer = consumerConfig.setUpConsumer(rideInfoEventsTopic, RideInfoMessage.class);
        RideInfoMessage message = DataUtil.defaultRideInfoMessage();

        sendRequestHandler.sendRideInfoRequestToKafka(message);
        ConsumerRecords<String, RideInfoMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(message));
    }

    @Test
    void sendDriverStatusRequestToKafka_shouldSendToDriverStatusEventsTopic() {
        Consumer<String, DriverStatusMessage> consumer = consumerConfig.setUpConsumer(driverStatusEventsTopic, DriverStatusMessage.class);
        DriverStatusMessage message = DataUtil.defaultDriverStatusMessageUnavailable();

        sendRequestHandler.sendDriverStatusRequestToKafka(message);
        ConsumerRecords<String, DriverStatusMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(message));
    }

    @Test
    void sendRidePaymentRequestToKafka_shouldSendToRidePaymentEventsTopic() {
        Consumer<String, RidePaymentMessage> consumer = consumerConfig.setUpConsumer(ridePaymentEventsTopic, RidePaymentMessage.class);
        RidePaymentMessage message = new RidePaymentMessage(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(4));

        sendRequestHandler.sendRidePaymentRequestToKafka(message);
        ConsumerRecords<String, RidePaymentMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(message));
    }
}
