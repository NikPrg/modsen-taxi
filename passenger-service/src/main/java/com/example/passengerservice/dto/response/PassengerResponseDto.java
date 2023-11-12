package com.example.passengerservice.dto.response;

import com.example.passengerservice.dto.model.CardDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;
import java.util.UUID;

public record PassengerResponseDto(
        UUID externalId,

        String firstName,

        String lastName,

        String phone,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String email,

        Double rate,

        Set<CardDto> cards
) {}
