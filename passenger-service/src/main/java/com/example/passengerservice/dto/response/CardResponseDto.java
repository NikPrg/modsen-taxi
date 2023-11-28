package com.example.passengerservice.dto.response;

import com.example.passengerservice.dto.model.PassengerCardDto;

import java.util.List;

public record CardResponseDto(
        List<PassengerCardDto> cards
) {
}
