package com.example.passengerservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import static com.example.passengerservice.util.RegexpConstants.*;
@Builder
public record ChangePhoneRequest(
        @Pattern(regexp = PHONE_REGEXP,
                message = "{phone.incorrectFormat}")
        String phone
) {
}
