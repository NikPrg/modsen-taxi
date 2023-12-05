package com.example.ridesservice.amqp.handler;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;

public interface SendRequestHandler {
    void sendRideInfoRequestToKafka(RideInfoMessage message);
    void sendDriverStatusRequestToKafka(DriverStatusMessage message);
}
