package com.example.passengerservice.amqp.channelGateway;


import com.example.passengerservice.amqp.handler.SendRequestHandler;
import com.example.passengerservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.amqp.message.RemovePassengerInfoMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "cardInfoKafkaChannel")
    void sendCardUsedAsDefaultChangeRequestToKafka(ChangeCardUsedAsDefaultMessage message);

    @Override
    @Gateway(requestChannel = "createPassengerInfoKafkaChannel")
    void sendNewPassengerToKafka(NewPassengerInfoMessage message);

    @Override
    @Gateway(requestChannel = "removePassengerInfoKafkaChannel")
    void sendPassengerRemovalToKafka(RemovePassengerInfoMessage message);
}