package com.example.cardservice.service;

import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.CreateCardResponse;

import java.util.UUID;

public interface CardService {
    CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId);
}
