package com.example.ridesservice.dto.response.ride;

import com.example.ridesservice.model.enums.RideStatus;

import java.util.UUID;

public record StartRideResponse(
        Long id,
        UUID externalId,
        RideStatus rideStatus
) {
}
