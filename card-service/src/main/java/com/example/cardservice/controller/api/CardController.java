package com.example.cardservice.controller.api;

import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.dto.response.DefaultCardResponse;
import com.example.cardservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.UUID;

import static com.example.cardservice.util.ApiRoutesConstants.*;

@RestController
@RequestMapping(PUBLIC_API_V1_PASSENGERS)
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping(PASSENGER_EXTERNAL_ID_CARDS)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCardResponse createCard(@PathVariable UUID passengerExternalId,
                                         @RequestBody @Valid CardRegistrationDto cardDto) {
        return cardService.create(cardDto, passengerExternalId);
    }

    @GetMapping(PASSENGER_EXTERNAL_ID_CARDS)
    @ResponseStatus(HttpStatus.OK)
    public AllCardsResponse findCardsByPassengerExternalId(@PathVariable UUID passengerExternalId) {
        return cardService.findCardsByPassengerExternalId(passengerExternalId);
    }

    @GetMapping(PASSENGER_EXTERNAL_ID_CARDS_DEFAULT)
    public DefaultCardResponse findDefaultCardByPassengerExternalId(@PathVariable UUID passengerExternalId) {
        return cardService.findDefaultCardByPassengerExternalId(passengerExternalId);
    }

    @DeleteMapping(PASSENGER_EXTERNAL_ID_CARDS_CARD_EXTERNAL_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassengerCard(@PathVariable UUID passengerExternalId,
                                    @PathVariable UUID cardExternalId) {
        cardService.deletePassengerCard(passengerExternalId, cardExternalId);
    }
}
