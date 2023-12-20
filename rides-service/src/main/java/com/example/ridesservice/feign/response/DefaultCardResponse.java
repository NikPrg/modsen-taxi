package com.example.ridesservice.feign.response;

import java.util.UUID;

public record DefaultCardResponse(
        UUID cardExternalId
) {
}
