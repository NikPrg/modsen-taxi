package com.example.ridesservice.dto.response.ride;

import com.example.ridesservice.model.enums.RideStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateRideResponse(
        Long id,
        UUID externalId,
        UUID passengerExternalId,
        String pickUpAddress,
        String destinationAddress,
        double rideCost,
        RideStatus rideStatus
) {
}
