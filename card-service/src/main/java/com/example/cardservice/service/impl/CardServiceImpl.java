package com.example.cardservice.service.impl;

import com.example.cardservice.amqp.handler.SendRequestHandler;
import com.example.cardservice.amqp.message.ChangeCardUsedAsDefaultMessage;
import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;

import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.exception.EntityAlreadyExistException;
import com.example.cardservice.exception.PassengerNotFoundException;
import com.example.cardservice.mapper.CardMapper;
import com.example.cardservice.mapper.PassengerInfoMapper;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerCard;
import com.example.cardservice.model.PassengerInfo;
import com.example.cardservice.repository.CardRepository;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.service.CardService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.cardservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepo;
    private final EntityManager entityManager;
    private final CardMapper cardMapper;
    private final PassengerInfoMapper passengerInfoMapper;
    private final PassengerInfoRepository passengerInfoRepo;
    private final SendRequestHandler sendRequestHandler;

    @Transactional
    @Override
    public CreateCardResponse create(CardRegistrationDto cardDto, UUID passengerExternalId) {
        var passengerInfo = passengerInfoRepo.findByExternalId(passengerExternalId)
                .orElseThrow(() -> new PassengerNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        UUID externalId = cardRepo.findByNumber(cardDto.number())
                .map(storedCard -> addCardIfNotExisted(passengerInfo, storedCard))
                .orElseGet(() -> createAndAddNewCard(cardDto, passengerInfo));

        return new CreateCardResponse(externalId);
    }

    @Override
    public AllCardsResponse findCardsByPassengerExternalId(UUID passengerExternalId) {
        var passengerInfo = passengerInfoRepo.findByExternalIdFetch(passengerExternalId)
                .orElseThrow(() -> new PassengerNotFoundException(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId)));

        return new AllCardsResponse(passengerInfo.getCards().stream()
                .map(PassengerCard::getCard)
                .map(cardMapper::toCardDto)
                .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void deletePassengerCard(UUID passengerExternalId, UUID cardExternalId) {
        var passengerInfo = passengerInfoRepo.findByExternalIdAndCardExternalId(passengerExternalId, cardExternalId)
                .orElseThrow(() ->
                        new PassengerNotFoundException(PASSENGER_WITH_SPECIFIED_CARD_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId, cardExternalId)));
        removeCard(passengerInfo);
    }
    @Transactional
    @Override
    public void setCardAsUsedDefault(ChangeCardUsedAsDefaultMessage message) {
        UUID passengerExternalId = message.passengerExternalId();
        UUID cardExternalId = message.cardExternalId();

        var passengerInfo = passengerInfoRepo.findByExternalIdAndCardExternalId(passengerExternalId, cardExternalId)
                .orElseThrow(() ->
                        new PassengerNotFoundException(PASSENGER_WITH_SPECIFIED_CARD_NOT_FOUND_EXCEPTION_MESSAGE.formatted(passengerExternalId, cardExternalId)));

        passengerInfoRepo.findByExternalIdFetch(passengerExternalId).stream()
                .map(PassengerInfo::getCards)
                .flatMap(Collection::stream)
                .forEach(passengerCard -> passengerCard.setUsedAsDefault(false));

        passengerInfo.getCards().get(0).setUsedAsDefault(true);

    }
    @Transactional
    @Override
    public void removeCardAsUsedDefault(ChangeCardUsedAsDefaultMessage message) {
        UUID passengerExternalId = message.passengerExternalId();

        passengerInfoRepo.findByExternalIdFetch(passengerExternalId).stream()
                .map(PassengerInfo::getCards)
                .flatMap(Collection::stream)
                .forEach(passengerCard -> passengerCard.setUsedAsDefault(false));
    }

    private void removeCard(PassengerInfo passengerInfo) {
        PassengerCard passengerCard = passengerInfo.getCards().get(0);

        if (passengerCard.isUsedAsDefault()) {
            removeCardAndNotifyDefaultPaymentMethodChange(passengerInfo, passengerCard);
        } else {
            removeCard(passengerInfo, passengerCard);
        }
    }

    private void removeCardAndNotifyDefaultPaymentMethodChange(PassengerInfo passengerInfo, PassengerCard passengerCard) {
        var card = passengerCard.getCard();
        passengerInfo.removeCard(card);
        sendRequestHandler.sendDefaultPaymentMethodChangeRequestToKafka(ChangeDefaultPaymentMethodMessage.builder()
                .passengerExternalId(passengerInfo.getExternalId())
                .build());
    }

    private void removeCard(PassengerInfo passengerInfo, PassengerCard passengerCard) {
        passengerInfo.removeCard(passengerCard.getCard());
    }


    private UUID addCardIfNotExisted(PassengerInfo passenger, Card storedCard) {
        UUID cardExternalId = storedCard.getExternalId();
        UUID passengerExternalId = passenger.getExternalId();

        if (cardRepo.existsCardForPassenger(cardExternalId, passengerExternalId)) {
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
