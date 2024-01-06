package com.example.e2eservice.feign.response.rides;

import com.example.e2eservice.entity.PaymentMethod;
import com.example.e2eservice.entity.RideStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record GetRideResponse(
        Long id,
        UUID externalId,
        UUID passengerExternalId,
        String pickUpAddress,
        String destinationAddress,
        double rideCost,
        long rideDuration,
        PaymentMethod paymentMethod,
        DriverInfoResponse driver,
        RideStatus rideStatus
) {
}
