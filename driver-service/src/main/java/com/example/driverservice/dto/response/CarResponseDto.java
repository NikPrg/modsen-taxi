package com.example.driverservice.dto.response;

import java.util.UUID;

public record CarResponseDto(
        Long id,
        UUID externalId,
        String licensePlate,
        String model,
        String color
) {
}