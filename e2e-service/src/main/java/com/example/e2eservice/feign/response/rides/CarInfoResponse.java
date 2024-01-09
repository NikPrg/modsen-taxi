package com.example.e2eservice.feign.response.rides;

public record CarInfoResponse(
        String carLicensePlate,
        String carModel,
        String carColor
) {
}
