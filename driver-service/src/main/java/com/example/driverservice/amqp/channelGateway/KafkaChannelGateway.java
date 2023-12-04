package com.example.driverservice.amqp.channelGateway;

import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.amqp.handler.SendRequestHandler;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "driverInfoKafkaChannel")
    void sendDriverInfoRequestToKafka(DriverInfoMessage message);

}
