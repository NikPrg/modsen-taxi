package com.example.passengerservice.amqp.handler;

import com.example.passengerservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.passengerservice.amqp.message.NewPassengerInfoMessage;
import com.example.passengerservice.amqp.message.RemovePassengerInfoMessage;

public interface SendRequestHandler {
    void sendCardUsedAsDefaultChangeRequestToKafka(ChangeCardUsedAsDefaultMessage message);

    void sendNewPassengerToKafka(NewPassengerInfoMessage message);

    void sendPassengerRemovalToKafka(RemovePassengerInfoMessage message);
}

