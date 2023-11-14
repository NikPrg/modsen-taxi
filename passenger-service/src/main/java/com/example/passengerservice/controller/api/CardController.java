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

import static com.example.passengerservice.util.HttpConstants.*;


@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("{passengerId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCardResponse createCard(@PathVariable UUID passengerId,
                                         @RequestBody @Valid CardRegistrationDto cardDto) {
        return cardService.create(cardDto, passengerId);
    }

    @GetMapping("{passengerId}/cards")
    @ResponseStatus(HttpStatus.OK)
    public CardResponseDto findCardByPassengerId(@PathVariable UUID passengerId) {
        return cardService.findCardsByPassengerExternalId(passengerId);
    }

    @DeleteMapping("{passengerId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassengerCard(@PathVariable UUID passengerId,
                                    @PathVariable UUID cardId) {
        cardService.deletePassengerCard(passengerId, cardId);
    }
}