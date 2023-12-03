package com.example.driverservice.dto.response;

import java.util.UUID;

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