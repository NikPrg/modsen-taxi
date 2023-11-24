package com.example.ridesservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DriverInfoResponse(
        String firstName,
        String lastName,
        CarInfoResponse carInfo
) {
}
