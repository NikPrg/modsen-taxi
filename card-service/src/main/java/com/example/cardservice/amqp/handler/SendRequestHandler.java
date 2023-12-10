package com.example.cardservice.amqp.handler;

import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;

public interface SendRequestHandler {
    void sendDefaultPaymentMethodChangeRequestToKafka(ChangeDefaultPaymentMethodMessage message);
}
