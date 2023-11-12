package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.Pattern;

public record CardRegistrationDto(
        @Pattern(regexp = "\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}",
        message = "Incorrect format for card number")
        String number
) {
}
