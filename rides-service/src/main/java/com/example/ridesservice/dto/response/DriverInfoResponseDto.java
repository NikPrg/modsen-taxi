package com.example.ridesservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverInfoResponseDto(
        String driverFirstName,
        String dirverLastName,
        String licensePlate,
        String model,
        String color
) {
}
