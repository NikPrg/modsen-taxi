package com.example.cardservice.service;

import com.example.cardservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.dto.response.DefaultCardResponse;

import java.util.UUID;

public interface CardService {
    CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId);

    AllCardsResponse findCardsByPassengerExternalId(UUID passengerExternalId);

    void deletePassengerCard(UUID passengerExternalId, UUID cardExternalId);

    void setCardAsUsedDefault(ChangeCardUsedAsDefaultMessage message);

    void removeCardAsUsedDefault(ChangeCardUsedAsDefaultMessage message);

    DefaultCardResponse findDefaultCardByPassengerExternalId(UUID passengerExternalId);
}
