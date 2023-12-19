package com.example.cardservice.dto.response;

import com.example.cardservice.dto.model.CardDto;

import java.util.List;

public record AllCardsResponse(
        List<CardDto> cards
) {
}
