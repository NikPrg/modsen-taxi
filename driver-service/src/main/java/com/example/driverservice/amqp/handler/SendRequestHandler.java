package com.example.driverservice.amqp.handler;

import com.example.driverservice.amqp.message.DriverInfoMessage;
import org.springframework.messaging.support.GenericMessage;

public interface SendRequestHandler {
    void sendDriverInfoRequestToKafka(GenericMessage<DriverInfoMessage> message);
}
