package com.example.ridesservice.dto.response;

import com.example.ridesservice.dto.model.CardDto;
import com.example.ridesservice.model.enums.PaymentMethod;

import java.util.Set;

public record PaymentInfoResponse(
        PaymentMethod paymentMethod,
        Set<CardDto> cards
) {
}
