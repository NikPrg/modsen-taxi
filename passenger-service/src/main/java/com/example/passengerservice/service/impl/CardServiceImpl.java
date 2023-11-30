package com.example.passengerservice.service.impl;

import com.example.passengerservice.dto.request.CardRegistrationDto;
import com.example.passengerservice.dto.response.CardResponseDto;
import com.example.passengerservice.dto.response.CreateCardResponse;
import com.example.passengerservice.exception.CardNotBelongPassengerException;
import com.example.passengerservice.exception.EntityAlreadyExistException;
import com.example.passengerservice.mapper.CardMapper;
import com.example.passengerservice.mapper.PassengerCardMapper;
import com.example.passengerservice.model.Card;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.PaymentMethod;
import com.example.passengerservice.repository.CardRepository;
import com.example.passengerservice.repository.PassengerRepository;
import com.example.passengerservice.service.CardService;
import jakarta.persistence.EntityManager;
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
    private final PassengerCardMapper passengerCardMapper;
    private final EntityManager entityManager;

    @Transactional
    @Override
    public CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        UUID externalId = cardRepo.findByNumber(cardDto.number())
                .map(storedCard -> addCardIfNotContained(passengerExternalId, passenger, storedCard))
                .orElseGet(() -> this.createAndAddNewCard(cardDto, passenger));

        return new CreateCardResponse(externalId);
    }

    @Override
    public CardResponseDto findCardsByPassengerExternalId(UUID passengerExternalId) {
        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        return new CardResponseDto(passenger.getCards().stream()
                .map(passengerCardMapper::toPassengerCardDto)
                .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void deletePassengerCard(UUID passengerExternalId, UUID cardExternalId) {
        var card = cardRepo.findByExternalId(cardExternalId)
                .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_EXCEPTION_MESSAGE.formatted(cardExternalId)));

        var passenger = passengerRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new EntityNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        card.getPassengers().stream()
                .filter(passCard -> passCard.getPassenger().equals(passenger))
                .findFirst()
                .orElseThrow(() -> new CardNotBelongPassengerException(CARD_NOT_BELONG_PASSENGER_EXCEPTION_MESSAGE.formatted(card.getExternalId(), passenger.getExternalId())));

        passenger.setDefaultPaymentMethod(PaymentMethod.CASH);
        passenger.removeCard(card);
    }

    private UUID addCardIfNotContained(UUID passengerExternalId, Passenger passenger, Card storedCard) {
        boolean isContained = storedCard.getPassengers().stream()
                .anyMatch(passengerCard -> passengerCard.getPassenger().equals(passenger));

        if (isContained) {
            throw new EntityAlreadyExistException(CARD_ALREADY_EXIST_EXCEPTION_MESSAGE.formatted(passengerExternalId));
        }
        passenger.addCard(storedCard);
        return storedCard.getExternalId();
    }

    private UUID createAndAddNewCard(CardRegistrationDto cardDto, Passenger passenger) {
        var newCard = cardMapper.toCard(cardDto);
        entityManager.persist(newCard);
        passenger.addCard(newCard);
        return newCard.getExternalId();
    }
}