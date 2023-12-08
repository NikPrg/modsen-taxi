package com.example.cardservice.dto.response;

import java.util.UUID;

public record PassengerResponse(
        Long id,
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        Double rate
) {
}
