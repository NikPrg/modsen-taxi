package com.example.cardservice.amqp.handler;

import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.cardservice.amqp.message.ErrorInfoMessage;

public interface SendRequestHandler {
    void sendDefaultPaymentMethodChangeRequestToKafka(ChangeDefaultPaymentMethodMessage message);
    void sendErrorInfoMessageToKafka(ErrorInfoMessage message);
}
