package com.example.paymentservice.service.impl;

import com.example.paymentservice.amqp.message.CardInfoMessage;
import com.example.paymentservice.model.CardBalance;
import com.example.paymentservice.repository.CardBalanceRepository;
import com.example.paymentservice.service.PaymentService;
import com.example.paymentservice.util.FakeBalanceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CardBalanceRepository cardBalanceRepo;

    @Override
    public void addNewCard(CardInfoMessage message) {
        UUID cardExternalId = message.cardExternalId();

        if (Boolean.FALSE.equals(cardBalanceRepo.existsByCardExternalId(cardExternalId))) {
            BigDecimal generatedCardBalance = FakeBalanceGenerator.generateCardBalance();
            CardBalance cardBalance = buildCardBalanceEntity(cardExternalId, generatedCardBalance);
            cardBalanceRepo.save(cardBalance);
        }
    }

    private static CardBalance buildCardBalanceEntity(UUID cardExternalId, BigDecimal generatedCardBalance) {
        return CardBalance.builder()
                .cardExternalId(cardExternalId)
                .balance(generatedCardBalance)
                .build();
    }
}
