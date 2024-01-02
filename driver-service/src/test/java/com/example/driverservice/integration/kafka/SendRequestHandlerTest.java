package com.example.driverservice.integration.kafka;

import com.example.driverservice.amqp.handler.SendRequestHandler;
import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.integration.TestcontainersBase;
import com.example.driverservice.integration.kafka.config.KafkaConsumerConfigTest;
import com.example.driverservice.util.DataUtil;
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

    @Value("${app.kafka.topic.driver-info-events}")
    private String driverInfoEventsTopic;

    @Test
    void sendDriverInfoRequestToKafka_shouldSendToDriverInfoEventsTopic(){
        Consumer<String, DriverInfoMessage> consumer = consumerConfig.setUpConsumer(driverInfoEventsTopic, DriverInfoMessage.class);
        DriverInfoMessage request = DataUtil.defaultDriverInfoMessageWithCar();

        sendRequestHandler.sendDriverInfoRequestToKafka(request);
        ConsumerRecords<String, DriverInfoMessage> records = consumer.poll(Duration.of(10, TimeUnit.SECONDS.toChronoUnit()));

        assertThat(records.count()).isEqualTo(1);
        records.forEach(r -> assertThat(r.value()).isEqualTo(request));
    }
}
