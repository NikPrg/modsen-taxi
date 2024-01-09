package com.example.driverservice.dto.response;

import com.example.driverservice.model.enums.DriverStatus;
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