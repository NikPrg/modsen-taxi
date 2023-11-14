package com.example.driverservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.example.driverservice.util.RegexpConstants.*;

public record CarRequestDto(

        @Pattern(regexp = LICENSE_PLATE_REGEXP,
                message = "{licensePlate.incorrectFormat}")
        String licensePlate,

        @NotBlank(message = "{carModel.notBlank}")
        String model,

        @NotBlank(message = "{carColor.notBlank}")
        String color
) {
}