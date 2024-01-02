package com.example.ridesservice.config.kafka.consumer;

import com.example.ridesservice.amqp.message.DriverInfoMessage;
import com.example.ridesservice.amqp.message.PaymentInfoMessage;
import com.example.ridesservice.model.enums.DriverStatus;
import com.example.ridesservice.service.DriverInfoService;
import com.example.ridesservice.service.RideService;
import com.example.ridesservice.util.KafkaUtils;
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
    private static final String DRIVER_STATUS_EXPRESSION = "payload.driverStatus";

    private final DriverInfoService driverInfoService;
    private final RideService rideService;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroup;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${app.kafka.topic.driver-info-events}")
    private String driverInfoEventsTopic;

    @Value("${app.kafka.topic.payment-result}")
    private String paymentResultTopic;

    @Bean
    public IntegrationFlow consumeDriverInfoFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, driverInfoEventsTopic))
                .route(DRIVER_STATUS_EXPRESSION, r -> r
                        .subFlowMapping(DriverStatus.CREATED, fl -> fl.handle(driverInfoService, "saveNewDriverData"))
                        .defaultOutputToParentFlow())
                .handle(driverInfoService, "updateDriverData")
                .get();
    }

    @Bean
    public IntegrationFlow consumePaymentResultFromKafka(ConsumerFactory<String, String> consumerFactory) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(consumerFactory, paymentResultTopic))
                .handle(rideService, "handlePaymentResult")
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
                DriverInfoMessage.class,
                PaymentInfoMessage.class
        );

        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        consumerProperties.put(JsonDeserializer.TYPE_MAPPINGS, typeMappings);

        return consumerProperties;
    }
}
