package com.example.passengerservice.controller.api;

import com.example.passengerservice.dto.request.CardRegistrationDto;
import com.example.passengerservice.dto.response.CardResponseDto;
import com.example.passengerservice.dto.response.CreateCardResponse;
import com.example.passengerservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.passengerservice.util.ApiRoutesConstants.*;


@RestController
@RequestMapping(PUBLIC_API_V1)
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
    public CardResponseDto findCardsByPassengerExternalId(@PathVariable UUID passengerExternalId) {
        return cardService.findCardsByPassengerExternalId(passengerExternalId);
    }

    @DeleteMapping("{passengerExternalId}/cards/{cardExternalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassengerCard(@PathVariable UUID passengerExternalId,
                                    @PathVariable UUID cardExternalId) {
        cardService.deletePassengerCard(passengerExternalId, cardExternalId);
    }
}