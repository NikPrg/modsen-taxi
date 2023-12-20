package com.example.ridesservice.dto.response.ride;

import com.example.ridesservice.dto.response.DriverInfoResponse;
import com.example.ridesservice.model.enums.PaymentMethod;
import com.example.ridesservice.model.enums.RideStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FinishRideResponse(
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
