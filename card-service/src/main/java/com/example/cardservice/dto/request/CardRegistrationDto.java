package com.example.cardservice.dto.request;

import jakarta.validation.constraints.Pattern;

import static com.example.cardservice.util.RegexpConstants.*;

public record CardRegistrationDto(
        @Pattern(regexp = CARD_REGEXP,
                message = "{card.incorrectFormat}")
        String number
) {
}
