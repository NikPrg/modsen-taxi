package com.example.passengerservice.service;

import com.example.passengerservice.dto.request.CardRegistrationDto;
import com.example.passengerservice.dto.response.CardResponseDto;
import com.example.passengerservice.dto.response.CreateCardResponse;

import java.util.UUID;

public interface CardService {

    CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId);

    CardResponseDto findCardsByPassengerExternalId(UUID passengerExternalId);

    void deletePassengerCard(UUID passengerExternalId, UUID cardExternalId);

}
