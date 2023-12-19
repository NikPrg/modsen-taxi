package com.example.cardservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import static com.example.cardservice.util.RegexpConstants.*;

@Builder
public record CardRegistrationDto(
        @Pattern(regexp = CARD_REGEXP,
                message = "{card.incorrectFormat}")
        String number
) {
}
