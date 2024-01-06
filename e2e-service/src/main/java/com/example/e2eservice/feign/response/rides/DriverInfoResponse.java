package com.example.e2eservice.feign.response.rides;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverInfoResponse(
        UUID externalId,
        String firstName,
        String lastName,
        CarInfoResponse carInfo
) {
}
