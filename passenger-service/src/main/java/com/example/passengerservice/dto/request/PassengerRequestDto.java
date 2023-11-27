package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.example.passengerservice.util.RegexpConstants.*;

public record PassengerRequestDto(
        @NotBlank(message = "{firstName.notBlank")
        @Size(min = 2, max = 100, message = "{firstName.size}")
        String firstName,
        @NotBlank(message = "{lastName.notBlank}")
        @Size(min = 2, max = 100, message = "{lastName.size}")
        String lastName
) {
}
