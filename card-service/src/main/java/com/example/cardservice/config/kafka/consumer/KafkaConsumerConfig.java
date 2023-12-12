package com.example.cardservice.config.kafka.consumer;

import com.example.cardservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.cardservice.amqp.message.NewPassengerInfoMessage;
import com.example.cardservice.amqp.message.RemovePassengerInfoMessage;
import com.example.cardservice.model.enums.PaymentMethod;
import com.example.cardservice.service.CardService;
import com.example.cardservice.service.PassengerInfoService;
import com.example.cardservice.util.KafkaUtils;
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
    private static final String PAYMENT_METHOD_TYPE_EXPRESSION = "payload.paymentMethod";
    private static final String TOPIC_TYPE_EXPRESSION = "headers['kafka_receivedTopic']";

    private final CardService cardService;
    private final PassengerInfoService passengerInfoService;

    @Value("${app.kafka.topic.card-default-status-details}")
    private String cardDefaultStatusDetailsTopic;

    @Value("${app.kafka.topic.new-passenger-details}")
    private String newPassengerDetailsTopic;

    @Value("${app.kafka.topic.removed-passenger-details}")
    private String removedPassengerDetailsTopic;

    @Bean
    public IntegrationFlow consumeCardStatusChangesFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, cardDefaultStatusDetailsTopic))
                .route(PAYMENT_METHOD_TYPE_EXPRESSION, r -> r
                        .subFlowMapping(PaymentMethod.CARD, fl -> fl.handle(cardService, "setCardAsUsedDefault"))
                        .subFlowMapping(PaymentMethod.CASH, fl -> fl.handle(cardService, "removeCardAsUsedDefault"))
                )
                .get();
    }

    @Bean
    public IntegrationFlow consumePassengerInfoFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, newPassengerDetailsTopic, removedPassengerDetailsTopic))
                .route(TOPIC_TYPE_EXPRESSION, r -> r
                        .subFlowMapping(newPassengerDetailsTopic, fl -> fl.handle(passengerInfoService, "saveNewPassengerInfo"))
                        .subFlowMapping(removedPassengerDetailsTopic, fl -> fl.handle(passengerInfoService, "removePassengerInfo"))
                )
                .get();
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(kafkaProperties));
    }

    @Bean
    public Map<String, Object> consumerConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        String typeMappings = KafkaUtils.buildTypeMappings(
                ChangeCardUsedAsDefaultMessage.class,
                NewPassengerInfoMessage.class,
                RemovePassengerInfoMessage.class
        );

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(JsonDeserializer.TYPE_MAPPINGS, typeMappings);

        return consumerProperties;
    }
}
