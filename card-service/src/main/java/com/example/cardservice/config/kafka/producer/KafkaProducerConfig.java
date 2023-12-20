package com.example.cardservice.config.kafka.producer;

import com.example.cardservice.amqp.message.CardInfoMessage;
import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.cardservice.amqp.message.ErrorInfoMessage;
import com.example.cardservice.util.KafkaUtils;
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
    private static final String PAYMENT_METHOD_INFO_KAFKA_CHANNEL = "paymentMethodInfoKafkaChannel";
    private static final String ERROR_INFO_KAFKA_CHANNEL = "errorInfoKafkaChannel";
    private static final String CREATE_CARD_INFO_KAFKA_CHANNEL = "createCardInfoKafkaChannel";

    @Value("${app.kafka.topic.payment-method-details}")
    private String paymentMethodDetailsTopic;

    @Value("${app.kafka.topic.new-card-details}")
    private String newCardDetailsTopic;

    @Value("${app.kafka.topic.error-card-details}")
    private String errorCardDetailsTopic;

    @Bean
    public IntegrationFlow sendToKafkaFlow(KafkaProperties kafkaProperties) {
        return f -> f
                .channel(PAYMENT_METHOD_INFO_KAFKA_CHANNEL)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(kafkaProperties))
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .headerMapper(mapper())
                        .topic(paymentMethodDetailsTopic))
                .channel(ERROR_INFO_KAFKA_CHANNEL)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(kafkaProperties))
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .headerMapper(mapper())
                        .topic(errorCardDetailsTopic))
                .channel(CREATE_CARD_INFO_KAFKA_CHANNEL)
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate(kafkaProperties))
                        .messageKey(m -> m.getHeaders().get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                        .headerMapper(mapper())
                        .topic(newCardDetailsTopic));
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
    public MessageChannel paymentMethodInfoKafkaChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel errorInfoKafkaChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel createCardInfoKafkaChannel(){
        return new DirectChannel();
    }

    @Bean
    public Map<String, Object> kafkaConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        String typeMappings = KafkaUtils.buildTypeMappings(
                ChangeDefaultPaymentMethodMessage.class,
                ErrorInfoMessage.class,
                CardInfoMessage.class
        );

        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProperties.put(JsonSerializer.TYPE_MAPPINGS, typeMappings);

        return producerProperties;
    }
}
