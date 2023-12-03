package com.example.driverservice.amqp.channelGateway;

import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.amqp.handler.SendRequestHandler;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.support.GenericMessage;

@MessagingGateway
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "driverInfoKafkaChannel")
    void sendDriverInfoRequestToKafka(GenericMessage<DriverInfoMessage> message);

}
