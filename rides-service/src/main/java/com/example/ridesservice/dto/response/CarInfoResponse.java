package com.example.ridesservice.dto.response;

public record CarInfoResponse(
        String carLicensePlate,
        String carModel,
        String carColor
) {
}
