package com.example.passengerservice.dto.response;

import com.example.passengerservice.dto.model.CardDto;

import java.util.Set;

public record CardResponseDto(
        Set<CardDto> cards
) {
}
