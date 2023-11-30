package com.example.passengerservice.dto.model;

public record PassengerCardDto(
        CardDto card,
        boolean usedAsDefault
) {
}
