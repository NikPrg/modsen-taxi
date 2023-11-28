package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.Pattern;

import static com.example.passengerservice.util.RegexpConstants.*;

public record CardRegistrationDto(
        @Pattern(regexp = CARD_REGEXP,
                message = "{card.incorrectFormat}")
        String number
) {
}
