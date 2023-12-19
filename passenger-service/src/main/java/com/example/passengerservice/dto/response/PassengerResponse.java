package com.example.passengerservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PassengerResponse(
        Long id,
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        Double rate
) {
}
