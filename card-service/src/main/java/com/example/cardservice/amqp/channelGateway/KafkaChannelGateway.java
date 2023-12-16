package com.example.cardservice.amqp.channelGateway;

import com.example.cardservice.amqp.handler.SendRequestHandler;
import com.example.cardservice.amqp.message.CardInfoMessage;
import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.cardservice.amqp.message.ErrorInfoMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "paymentMethodInfoKafkaChannel")
    void sendDefaultPaymentMethodChangeRequestToKafka(ChangeDefaultPaymentMethodMessage message);

    @Override
    @Gateway(requestChannel = "createCardInfoKafkaChannel")
    void sendNewCardInfoToKafka(CardInfoMessage message);

    @Override
    @Gateway(requestChannel = "errorInfoKafkaChannel")
    void sendErrorInfoMessageToKafka(ErrorInfoMessage message);

}