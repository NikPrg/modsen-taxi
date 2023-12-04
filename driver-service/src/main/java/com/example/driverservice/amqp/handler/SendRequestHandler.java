package com.example.driverservice.amqp.handler;

import com.example.driverservice.amqp.message.DriverInfoMessage;

public interface SendRequestHandler {
    void sendDriverInfoRequestToKafka(DriverInfoMessage message);
}
