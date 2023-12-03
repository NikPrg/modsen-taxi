package com.example.ridesservice.amqp.handler;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import org.springframework.messaging.support.GenericMessage;

public interface SendRequestHandler {
    void sendRideInfoRequestToKafka(GenericMessage<RideInfoMessage> message);
    void sendDriverStatusRequestToKafka(GenericMessage<DriverStatusMessage> message);
}
