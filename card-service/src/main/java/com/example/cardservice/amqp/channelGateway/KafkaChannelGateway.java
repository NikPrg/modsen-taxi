package com.example.cardservice.amqp.channelGateway;

import com.example.cardservice.amqp.handler.SendRequestHandler;
import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "paymentMethodInfoKafkaChannel")
    void sendDefaultPaymentMethodChangeRequestToKafka(ChangeDefaultPaymentMethodMessage message);

}