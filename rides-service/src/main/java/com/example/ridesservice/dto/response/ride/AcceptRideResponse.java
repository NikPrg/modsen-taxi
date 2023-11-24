package com.example.ridesservice.dto.response.ride;

import com.example.ridesservice.dto.response.DriverInfoResponse;
import com.example.ridesservice.model.enums.RideStatus;

import java.util.UUID;

public record AcceptRideResponse(
        Long id,
        UUID externalId,
        double rideCost,
        RideStatus rideStatus,
        DriverInfoResponse driver
) {
}
