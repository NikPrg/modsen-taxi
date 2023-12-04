package com.example.ridesservice.amqp.channelGateway;

import com.example.ridesservice.amqp.handler.SendRequestHandler;
import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface KafkaChannelGateway extends SendRequestHandler {
    @Override
    @Gateway(requestChannel = "rideInfoKafkaChannel")
    void sendRideInfoRequestToKafka(RideInfoMessage message);

    @Override
    @Gateway(requestChannel = "driverStatusKafkaChannel")
    void sendDriverStatusRequestToKafka(DriverStatusMessage message);
}
