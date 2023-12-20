package com.example.cardservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCardResponse(
        UUID cardExternalId
) {
}
