package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.CardRegistrationDto;
import com.example.passengerservice.dto.response.CardResponseDto;
import com.example.passengerservice.dto.response.CreateCardResponse;
import com.example.passengerservice.exception.EntityAlreadyExistException;
import com.example.passengerservice.mapper.CardMapper;
import com.example.passengerservice.model.PaymentMethod;
import com.example.passengerservice.repository.CardRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.CardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.passengerservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepo;
    private final PassengerRepository passengerRepo;
    private final CardMapper cardMapper;

    @Transactional
    @Override
    public CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId) {

        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_ERROR_MESSAGE.formatted(passengerExternalId)));

        UUID externalId = cardRepo.findByNumber(cardDto.number())
                .map(storedCard -> {
                    if (storedCard.getPassengers().contains(passenger)) {
                        throw new EntityAlreadyExistException(CARD_ALREADY_EXIST_ERROR_MESSAGE.formatted(passengerExternalId));
                    }
                    passenger.addCard(storedCard);
                    return storedCard.getExternalId();
                })
                .orElseGet(() -> {
                    var newCard = cardMapper.toCard(cardDto);
                    passenger.addCard(newCard);
                    return newCard.getExternalId();
                });

        return new CreateCardResponse(externalId);
    }

    @Override
    public CardResponseDto findCardsByPassengerExternalId(UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_ERROR_MESSAGE.formatted(passengerExternalId)));

        return new CardResponseDto(passenger.getCards().stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toSet()));
    }

    @Transactional
    @Override
    public void deletePassengerCard(UUID passengerExternalId, UUID cardExternalId) {
        var card = cardRepo.findByExternalId(cardExternalId)
                .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_ERROR_MESSAGE.formatted(cardExternalId)));

        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_ERROR_MESSAGE.formatted(passengerExternalId)));

        if (passenger.getDefaultPaymentMethod().getCardNumber().equals(card.getNumber())) {
            passenger.setDefaultPaymentMethod(PaymentMethod.CASH);
            
        }

        passenger.removeCard(card);
    }
}