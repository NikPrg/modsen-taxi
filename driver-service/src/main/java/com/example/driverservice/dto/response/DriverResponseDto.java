package com.example.driverservice.dto.response;

import java.util.UUID;

public record DriverResponseDto(
        Long id,
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        Double rate,
        CarResponseDto car
) {
}