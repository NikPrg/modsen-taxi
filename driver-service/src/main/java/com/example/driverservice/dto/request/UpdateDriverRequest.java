package com.example.driverservice.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.example.driverservice.util.RegexpConstants.*;
@Builder
public record UpdateDriverRequest(
        @Size(min = 2, max = 100, message = "{firstName.size}")
        String firstName,
        @Size(min = 2, max = 100, message = "{lastName.size}")
        String lastName,
        @Pattern(regexp = PHONE_REGEXP,
                message = "{phone.incorrectFormat}")
        String phone
) {
}
