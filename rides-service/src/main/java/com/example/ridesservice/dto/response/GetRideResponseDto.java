package com.example.ridesservice.dto.response;

import com.example.ridesservice.model.enums.PaymentMethod;

import java.time.LocalTime;
import java.util.UUID;

public record GetRideResponseDto(
        Long id,
        UUID externalId,
        String pickUpAddress,
        String destinationAddress,
        double rideCost,
        PaymentMethod paymentMethod,
        LocalTime rideDuration,
        DriverInfoResponseDto driver
) {
}
