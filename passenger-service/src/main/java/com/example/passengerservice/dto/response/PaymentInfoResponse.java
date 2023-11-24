package com.example.passengerservice.dto.response;

import com.example.passengerservice.dto.model.CardDto;
import com.example.passengerservice.model.PaymentMethod;

import java.util.Set;

public record PaymentInfoResponse(
        PaymentMethod paymentMethod,
        Set<CardDto> cards
) {
}
