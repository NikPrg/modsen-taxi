package com.example.paymentservice.config.kafka;

import com.example.paymentservice.amqp.message.CardInfoMessage;
import com.example.paymentservice.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaCloudStreamConfig {
    @Bean
    public Consumer<CardInfoMessage> consumeCardInfo(PaymentService paymentService){
        return paymentService::addNewCard;
    }
}
