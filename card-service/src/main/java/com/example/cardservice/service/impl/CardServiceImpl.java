package com.example.cardservice.service.impl;

import com.example.cardservice.client.passenger.PassengerClient;
import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.dto.response.PassengerResponse;
import com.example.cardservice.exception.EntityAlreadyExistException;
import com.example.cardservice.mapper.CardMapper;
import com.example.cardservice.mapper.PassengerInfoMapper;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerInfo;
import com.example.cardservice.repository.CardRepository;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.service.CardService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.cardservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepo;
    private final EntityManager entityManager;
    private final CardMapper cardMapper;
    private final PassengerClient passengerClient;
    private final PassengerInfoMapper passengerInfoMapper;
    private final PassengerInfoRepository passengerInfoRepo;

    @Transactional
    @Override
    public CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId) {
        PassengerResponse passengerResponse = passengerClient.findPassengerByExternalId(passengerExternalId);
        PassengerInfo passengerInfo = passengerInfoRepo.findByExternalId(passengerResponse.externalId())
                .orElseGet(() -> persistNewPassenger(passengerResponse));

        UUID externalId = cardRepo.findByNumber(cardDto.number())
                .map(storedCard -> addCardIfNotContained(passengerInfo, storedCard))
                .orElseGet(() -> createAndAddNewCard(cardDto, passengerInfo));

        return new CreateCardResponse(externalId);
    }

    private PassengerInfo persistNewPassenger(PassengerResponse passengerResponse) {
        var passInfo = passengerInfoMapper.toPassengerInfo(passengerResponse);
        entityManager.persist(passInfo);
        return passInfo;
    }

    private UUID addCardIfNotContained(PassengerInfo passenger, Card storedCard) {
        UUID cardExternalId = storedCard.getExternalId();
        UUID passengerExternalId = passenger.getExternalId();

        boolean existed = cardRepo.existsCardForPassenger(cardExternalId, passengerExternalId)
                .isPresent();
        if (existed) {
            throw new EntityAlreadyExistException(CARD_ALREADY_EXIST_PASSENGER_EXCEPTION_MESSAGE.formatted(passengerExternalId));
        }

        passenger.addCard(storedCard);
        return cardExternalId;
    }

    private UUID createAndAddNewCard(CardRegistrationDto cardDto, PassengerInfo passenger) {
        var newCard = cardMapper.toCard(cardDto);
        entityManager.persist(newCard);
        passenger.addCard(newCard);
        return newCard.getExternalId();
    }
}
