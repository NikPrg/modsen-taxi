package com.example.e2eservice.feign.response.driver;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CarResponse(
        Long id,
        UUID externalId,
        String licensePlate,
        String model,
        String color
) {
}