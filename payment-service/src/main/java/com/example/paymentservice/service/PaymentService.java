package com.example.paymentservice.service;

import com.example.paymentservice.amqp.message.CardInfoMessage;
import com.example.paymentservice.amqp.message.RidePaymentMessage;
import com.example.paymentservice.amqp.message.PaymentInfoMessage;

public interface PaymentService {
    void addNewCard(CardInfoMessage message);
    PaymentInfoMessage processPayment(RidePaymentMessage message);
}
