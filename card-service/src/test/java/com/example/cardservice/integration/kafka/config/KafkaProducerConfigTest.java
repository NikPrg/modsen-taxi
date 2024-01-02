package com.example.cardservice.integration.kafka.config;

import com.example.cardservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.cardservice.amqp.message.NewPassengerInfoMessage;
import com.example.cardservice.util.KafkaUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfigTest {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, Object> testKafkaTemplate() {
        return new KafkaTemplate<>(testProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> testProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> producerProperties = new HashMap<>();
        String typeMappings = KafkaUtils.buildTypeMappings(
                ChangeCardUsedAsDefaultMessage.class,
                NewPassengerInfoMessage.class
        );

        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProperties.put(JsonSerializer.TYPE_MAPPINGS, typeMappings);

        return producerProperties;
    }
}