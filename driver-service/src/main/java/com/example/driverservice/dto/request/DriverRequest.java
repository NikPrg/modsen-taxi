package com.example.driverservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.example.driverservice.util.RegexpConstants.*;
@Builder
public record DriverRequest(
        @NotBlank(message = "{firstName.notBlank}")
        @Size(min = 2, max = 100, message = "{firstName.size}")
        String firstName,
        @NotBlank(message = "{lastName.notBlank}")
        @Size(min = 2, max = 100, message = "{lastName.size}")
        String lastName,
        @Pattern(regexp = PHONE_REGEXP,
                message = "{phone.incorrectFormat}")
        String phone
) {
}