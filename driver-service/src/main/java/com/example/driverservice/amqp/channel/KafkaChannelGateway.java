package com.example.driverservice.amqp.channel;

import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.service.SendRequestHandler;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "sendToKafkaChannel")
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "sendToKafkaChannel")
    void handleRequest(DriverInfoMessage request);
}
