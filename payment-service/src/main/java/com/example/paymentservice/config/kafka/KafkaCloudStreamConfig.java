package com.example.paymentservice.config.kafka;

import com.example.paymentservice.amqp.message.CardInfoMessage;
import com.example.paymentservice.amqp.message.RidePaymentMessage;
import com.example.paymentservice.amqp.message.PaymentInfoMessage;
import com.example.paymentservice.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class KafkaCloudStreamConfig {
    @Bean
    public Consumer<CardInfoMessage> consumeCardInfo(PaymentService paymentService) {
        return paymentService::addNewCard;
    }

    @Bean
    public Function<RidePaymentMessage, Message<PaymentInfoMessage>> processRidePayment(PaymentService paymentService) {
        return r -> MessageBuilder
                .withPayload(paymentService.processPayment(r))
                .setHeader(DefaultJackson2JavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME, PaymentInfoMessage.class.getSimpleName())
                .build();
    }

}
