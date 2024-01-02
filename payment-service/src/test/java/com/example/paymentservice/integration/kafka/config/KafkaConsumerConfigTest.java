package com.example.paymentservice.integration.kafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfigTest {
    private static final String CONSUMER_GROUP_TEST = "payment-service-test";
    private static final String CONSUMER_AUTO_OFFSET_RESET = "earliest";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public <T> Consumer<String, T> setUpConsumer(String topicName, Class<T> valueType) {
        Consumer<String, T> consumer = new KafkaConsumer<>(consumerConfigs(topicName));
        consumer.subscribe(Collections.singletonList(topicName));
        return consumer;
    }

    private Map<String, Object> consumerConfigs(String topicName) {
        var testConsumerGroup = topicName + CONSUMER_GROUP_TEST;

        Map<String, Object> consumerProperties = new HashMap<>();

        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, CONSUMER_AUTO_OFFSET_RESET);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, testConsumerGroup);
        consumerProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProperties.put(JsonDeserializer.TYPE_MAPPINGS, "");

        return consumerProperties;
    }
}

