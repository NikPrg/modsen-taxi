package com.example.ridesservice.config.kafka.producer;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.util.KafkaUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    private static final String RIDE_INFO_KAFKA_CHANNEL = "rideInfoKafkaChannel";
    private static final String DRIVER_STATUS_KAFKA_CHANNEL = "driverStatusKafkaChannel";

    @Value("${app.kafka.topic.ride-info-events}")
    private String rideInfoEventsTopic;

    @Value("${app.kafka.topic.driver-status-events}")
    private String driverStatusEventsTopic;

    @Bean
    public IntegrationFlow sendToKafkaFlow(KafkaProperties kafkaProperties) {
        return f -> f
                .channel(RIDE_INFO_KAFKA_CHANNEL)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(kafkaProperties))
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .headerMapper(mapper())
                        .topic(rideInfoEventsTopic))
                .channel(DRIVER_STATUS_KAFKA_CHANNEL)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(kafkaProperties))
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .headerMapper(mapper())
                        .topic(driverStatusEventsTopic));
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(producerFactory(kafkaProperties));
    }

    @Bean
    public DefaultKafkaHeaderMapper mapper() {
        return new DefaultKafkaHeaderMapper();
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigs(kafkaProperties));
    }

    @Bean
    public MessageChannel rideInfoKafkaChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel driverStatusKafkaChannel() {
        return new DirectChannel();
    }

    @Bean
    public Map<String, Object> kafkaConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        String typeMappings = KafkaUtils.buildTypeMappings(RideInfoMessage.class, DriverStatusMessage.class);

        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProperties.put(JsonSerializer.TYPE_MAPPINGS, typeMappings);

        return producerProperties;
    }
}
