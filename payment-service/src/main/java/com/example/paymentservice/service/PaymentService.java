package com.example.paymentservice.service;

import com.example.paymentservice.amqp.message.CardInfoMessage;

public interface PaymentService {
    void addNewCard(CardInfoMessage message);
}
