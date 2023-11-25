package com.example.ridesservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRideRequest(
        @NotBlank(message = "{pickUpAddress.notBlank")
        String pickUpAddress,
        @NotBlank(message = "{destinationAddress.notBlank")
        String destinationAddress
) {
}
