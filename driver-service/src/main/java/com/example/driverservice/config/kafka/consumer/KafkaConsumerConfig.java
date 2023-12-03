package com.example.driverservice.config.kafka.consumer;


import com.example.driverservice.amqp.message.DriverStatusMessage;
import com.example.driverservice.amqp.message.RideInfoMessage;
import com.example.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final DriverService driverService;

    @Bean
    public IntegrationFlow consumeNotificationFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, "ride-info-events"))
                .handle(driverService, "notificationDrivers")
                .get();

    }

    @Bean
    public IntegrationFlow consumeDriverStatusFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, "driver-status-events"))
                .handle(driverService, "updateDriverStatus")
                .get();
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TYPE_MAPPINGS,
                "rideInfoMessage:" + RideInfoMessage.class.getName() + "," +
                        "driverStatusMessage:" + DriverStatusMessage.class.getName(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-group"
        );
    }

}
