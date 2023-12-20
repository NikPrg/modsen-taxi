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

    @PostMapping("{passengerExternalId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCardResponse createCard(@PathVariable UUID passengerExternalId,
                                         @RequestBody @Valid CardRegistrationDto cardDto) {
        return cardService.create(cardDto, passengerExternalId);
    }

    @GetMapping("{passengerExternalId}/cards")
    @ResponseStatus(HttpStatus.OK)
    public AllCardsResponse findCardsByPassengerExternalId(@PathVariable UUID passengerExternalId) {
        return cardService.findCardsByPassengerExternalId(passengerExternalId);
    }

    @GetMapping("{passengerExternalId}/cards/default")
    public DefaultCardResponse findDefaultCardByPassengerExternalId(@PathVariable UUID passengerExternalId) {
        return cardService.findDefaultCardByPassengerExternalId(passengerExternalId);
    }

    @DeleteMapping("{passengerExternalId}/cards/{cardExternalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassengerCard(@PathVariable UUID passengerExternalId,
                                    @PathVariable UUID cardExternalId) {
        cardService.deletePassengerCard(passengerExternalId, cardExternalId);
    }
}
