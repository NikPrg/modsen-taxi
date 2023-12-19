package com.example.driverservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DriverResponse(
        Long id,
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        Double rate,
        CarResponse car
) {
}