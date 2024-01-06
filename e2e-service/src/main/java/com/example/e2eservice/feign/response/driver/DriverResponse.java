package com.example.e2eservice.feign.response.driver;

import com.example.e2eservice.entity.DriverStatus;
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
        DriverStatus driverStatus,
        CarResponse car
) {
}