package com.example.ridesservice.dto.response.error;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        String id,
        String message,
        LocalDateTime timestamp
) {
}
