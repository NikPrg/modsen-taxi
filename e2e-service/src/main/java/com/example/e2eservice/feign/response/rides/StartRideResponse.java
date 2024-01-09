package com.example.e2eservice.feign.response.rides;

import com.example.e2eservice.entity.RideStatus;

import java.util.UUID;

public record StartRideResponse(
        Long id,
        UUID externalId,
        RideStatus rideStatus
) {
}
