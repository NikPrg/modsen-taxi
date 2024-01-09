package com.example.driverservice.dto.response.error;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        String id,
        String message,
        int statusCode,
        LocalDateTime timestamp
) {
}