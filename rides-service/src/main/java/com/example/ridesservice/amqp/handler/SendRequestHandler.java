package com.example.ridesservice.amqp.handler;

import com.example.ridesservice.amqp.message.DriverStatusMessage;
import com.example.ridesservice.amqp.message.RideInfoMessage;
import com.example.ridesservice.amqp.message.RidePaymentMessage;

public interface SendRequestHandler {
    void sendRideInfoRequestToKafka(RideInfoMessage message);
    void sendDriverStatusRequestToKafka(DriverStatusMessage message);
    void sendRidePaymentRequestToKafka(RidePaymentMessage message);
}
