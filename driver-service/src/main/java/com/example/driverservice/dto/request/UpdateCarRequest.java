package com.example.driverservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import static com.example.driverservice.util.RegexpConstants.*;

@Builder
public record UpdateCarRequest(
        @Pattern(regexp = LICENSE_PLATE_REGEXP,
                message = "{licensePlate.incorrectFormat}")
        String licensePlate,
        String model,
        String color
) {
}
