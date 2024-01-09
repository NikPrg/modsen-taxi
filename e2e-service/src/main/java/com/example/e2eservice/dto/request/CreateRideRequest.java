package com.example.e2eservice.dto.request;

import lombok.Builder;

@Builder
public record CreateRideRequest(
        String pickUpAddress,
        String destinationAddress
) {
}
