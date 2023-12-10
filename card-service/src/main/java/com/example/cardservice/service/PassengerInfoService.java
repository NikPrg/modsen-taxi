package com.example.cardservice.service;

import com.example.cardservice.amqp.message.NewPassengerInfoMessage;
import com.example.cardservice.amqp.message.RemovePassengerInfoMessage;

public interface PassengerInfoService {
    void saveNewPassengerInfo(NewPassengerInfoMessage message);
    void removePassengerInfo(RemovePassengerInfoMessage message);
}
