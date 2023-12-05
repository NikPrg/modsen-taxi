package com.example.driverservice.config.kafka.consumer;

import com.example.driverservice.amqp.message.DriverStatusMessage;
import com.example.driverservice.amqp.message.RideInfoMessage;
import com.example.driverservice.service.DriverService;
import com.example.driverservice.util.KafkaUtils;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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

    @Value("${app.kafka.topic.ride-info-events}")
    private String rideInfoEventsTopic;

    @Value("${app.kafka.topic.driver-status-events}")
    private String driverStatusEventsTopic;

    @Bean
    public IntegrationFlow consumeNotificationFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, rideInfoEventsTopic))
                .handle(driverService, "notifyDrivers")
                .get();
    }

    @Bean
    public IntegrationFlow consumeDriverStatusFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, driverStatusEventsTopic))
                .handle(driverService, "updateDriverStatus")
                .get();
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(kafkaProperties));
    }

    @Bean
    public Map<String, Object> consumerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        String typeMappings = KafkaUtils.buildTypeMappings(RideInfoMessage.class, DriverStatusMessage.class);

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(JsonDeserializer.TYPE_MAPPINGS, typeMappings);

        return consumerProperties;
    }
}

