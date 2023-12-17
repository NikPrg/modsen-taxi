package com.example.paymentservice.service.impl;

import com.example.paymentservice.amqp.message.CardInfoMessage;
import com.example.paymentservice.amqp.message.PaymentInfoMessage;
import com.example.paymentservice.amqp.message.RidePaymentMessage;
import com.example.paymentservice.model.CardBalance;
import com.example.paymentservice.model.enums.PaymentStatus;
import com.example.paymentservice.repository.CardBalanceRepository;
import com.example.paymentservice.service.PaymentService;
import com.example.paymentservice.util.FakeBalanceGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.example.paymentservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CardBalanceRepository cardBalanceRepo;

    @Transactional
    @Override
    public void addNewCard(CardInfoMessage message) {
        UUID cardExternalId = message.cardExternalId();

        if (Boolean.FALSE.equals(cardBalanceRepo.existsByCardExternalId(cardExternalId))) {
            BigDecimal generatedCardBalance = FakeBalanceGenerator.generateCardBalance();
            CardBalance cardBalance = buildCardBalanceEntity(cardExternalId, generatedCardBalance);
            cardBalanceRepo.save(cardBalance);
        }
    }

    @Transactional
    @Override
    public PaymentInfoMessage processPayment(RidePaymentMessage message) {
        UUID cardExternalId = message.cardExternalId();

        var balance = cardBalanceRepo.findByCardExternalId(cardExternalId)
                .orElseThrow(() ->
                        new EntityNotFoundException(CARD_BALANCE_NOT_FOUND_ERROR_MESSAGE.formatted(cardExternalId)));

        return balance.getBalance().doubleValue() < message.rideCost().doubleValue()
                ? processRejectedPayment(message)
                : processSuccessfulPayment(message, balance);
    }

    private static CardBalance buildCardBalanceEntity(UUID cardExternalId, BigDecimal generatedCardBalance) {
        return CardBalance.builder()
                .cardExternalId(cardExternalId)
                .balance(generatedCardBalance)
                .build();
    }

    private PaymentInfoMessage processRejectedPayment(RidePaymentMessage message) {
        return PaymentInfoMessage.builder()
                .cardExternalId(message.cardExternalId())
                .rideExternalId(message.rideExternalId())
                .paymentStatus(PaymentStatus.FAILED)
                .build();
    }

    private PaymentInfoMessage processSuccessfulPayment(RidePaymentMessage message, CardBalance balance) {
        BigDecimal newBalance = balance.getBalance().subtract(message.rideCost());
        balance.setBalance(newBalance);
        cardBalanceRepo.save(balance);
        return PaymentInfoMessage.builder()
                .cardExternalId(message.cardExternalId())
                .rideExternalId(message.rideExternalId())
                .paymentStatus(PaymentStatus.PAID)
                .build();
    }

}
