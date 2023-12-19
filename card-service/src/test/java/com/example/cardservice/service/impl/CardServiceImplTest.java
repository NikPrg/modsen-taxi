package com.example.cardservice.service.impl;

import com.example.cardservice.amqp.handler.SendRequestHandler;
import com.example.cardservice.amqp.message.CardInfoMessage;
import com.example.cardservice.dto.model.CardDto;
import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.exception.EntityAlreadyExistException;
import com.example.cardservice.mapper.CardMapper;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerInfo;
import com.example.cardservice.repository.CardRepository;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.util.DataUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private PassengerInfoRepository passengerInfoRepository;
    @Mock
    private SendRequestHandler sendRequestHandler;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void create_shouldAddExistedCardAndReturnExpectedResponse() {
        CardRegistrationDto cardRegistrationDto = DataUtil.defaultCardRegistrationDto();
        PassengerInfo passengerInfo = DataUtil.initPassengerInfo();
        Card card = DataUtil.defaultCard();
        CardInfoMessage message = DataUtil.defaultCardInfoMessage();
        CreateCardResponse expected = DataUtil.defaultCreateCardResponse();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepository)
                .findByExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID);
        doReturn(Optional.of(card))
                .when(cardRepository)
                .findByNumber(DataUtil.CARD_NUMBER);
        doReturn(Boolean.FALSE)
                .when(cardRepository)
                .existsCardForPassenger(DataUtil.CARD_EXTERNAL_ID, DataUtil.PASSENGER_INFO_EXTERNAL_ID);
        doNothing()
                .when(sendRequestHandler)
                .sendNewCardInfoToKafka(message);

        CreateCardResponse actual = cardService.create(cardRegistrationDto, DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);

        verify(passengerInfoRepository).findByExternalId(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
        verify(cardRepository).findByNumber(eq(DataUtil.CARD_NUMBER));
        verify(cardRepository).existsCardForPassenger(eq(DataUtil.CARD_EXTERNAL_ID), eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
        verify(sendRequestHandler).sendNewCardInfoToKafka(eq(message));
    }

    @Test
    void create_shouldCreateNewCardAndReturnExpectedResponse() {
        CardRegistrationDto cardRegistrationDto = DataUtil.defaultCardRegistrationDto();
        PassengerInfo passengerInfo = DataUtil.initPassengerInfo();
        Card card = DataUtil.defaultCard();
        CardInfoMessage message = DataUtil.defaultCardInfoMessage();
        CreateCardResponse expected = DataUtil.defaultCreateCardResponse();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepository)
                .findByExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID);
        doReturn(Optional.empty())
                .when(cardRepository)
                .findByNumber(DataUtil.CARD_NUMBER);
        doReturn(card)
                .when(cardMapper)
                .toCard(cardRegistrationDto);
        doNothing()
                .when(sendRequestHandler)
                .sendNewCardInfoToKafka(message);

        CreateCardResponse actual = cardService.create(cardRegistrationDto, DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);

        verify(passengerInfoRepository).findByExternalId(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
        verify(cardRepository).findByNumber(eq(DataUtil.CARD_NUMBER));
        verify(cardMapper).toCard(eq(cardRegistrationDto));
        verify(entityManager).persist(eq(card));
        verify(sendRequestHandler).sendNewCardInfoToKafka(eq(message));
    }

    @Test
    void create_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerInfoRepository)
                .findByExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> cardService.create(DataUtil.defaultCardRegistrationDto(), DataUtil.PASSENGER_INFO_EXTERNAL_ID));

        verify(passengerInfoRepository).findByExternalId(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
    }

    @Test
    void create_shouldThrowEntityAlreadyExistException() {
        PassengerInfo passengerInfo = DataUtil.defaultPassengerInfoWithCard();
        Card card = DataUtil.defaultCard();
        CardRegistrationDto cardRegistrationDto = DataUtil.defaultCardRegistrationDto();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepository)
                .findByExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID);
        doReturn(Optional.of(card))
                .when(cardRepository)
                .findByNumber(DataUtil.CARD_NUMBER);
        doReturn(Boolean.TRUE)
                .when(cardRepository)
                .existsCardForPassenger(DataUtil.CARD_EXTERNAL_ID, DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThrows(EntityAlreadyExistException.class,
                () -> cardService.create(cardRegistrationDto, DataUtil.PASSENGER_INFO_EXTERNAL_ID));

        verify(passengerInfoRepository).findByExternalId(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
        verify(cardRepository).findByNumber(eq(DataUtil.CARD_NUMBER));
        verify(cardRepository).existsCardForPassenger(eq(DataUtil.CARD_EXTERNAL_ID), eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
    }

    @Test
    void findCardsByPassengerExternalId_shouldReturnExpectedResponse() {
        PassengerInfo passengerInfo = DataUtil.defaultPassengerInfoWithCard();
        Card card = DataUtil.defaultCard();
        CardDto cardDto = DataUtil.defaultCardDto();
        AllCardsResponse expected = DataUtil.defaultAllCardsResponse();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepository)
                .findByExternalIdFetch(DataUtil.PASSENGER_INFO_EXTERNAL_ID);
        doReturn(cardDto)
                .when(cardMapper)
                .toCardDto(card);

        AllCardsResponse actual = cardService.findCardsByPassengerExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);

        verify(passengerInfoRepository).findByExternalIdFetch(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
        verify(cardMapper).toCardDto(eq(card));
    }

    @Test
    void findCardsByPassengerExternalId_shouldReturnEmptyExpectedResponse() {
        PassengerInfo passengerInfo = DataUtil.initPassengerInfo();
        AllCardsResponse expected = DataUtil.defaultAllCardsResponseEmpty();

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepository)
                .findByExternalIdFetch(DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        AllCardsResponse actual = cardService.findCardsByPassengerExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual.cards()).isEmpty();

        verify(passengerInfoRepository).findByExternalIdFetch(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
    }

    @Test
    void findCardsByPassengerExternalId_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(passengerInfoRepository)
                .findByExternalIdFetch(DataUtil.PASSENGER_INFO_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> cardService.findCardsByPassengerExternalId(DataUtil.PASSENGER_INFO_EXTERNAL_ID));

        verify(passengerInfoRepository).findByExternalIdFetch(eq(DataUtil.PASSENGER_INFO_EXTERNAL_ID));
    }

    @Test
    void deletePassengerCard_shouldCallDeleteMethod(){
        PassengerInfo passengerInfo = DataUtil.defaultPassengerInfoWithCard();
        UUID passengerInfoExternalId = DataUtil.PASSENGER_INFO_EXTERNAL_ID;
        UUID cardExternalId = DataUtil.CARD_EXTERNAL_ID;

        doReturn(Optional.of(passengerInfo))
                .when(passengerInfoRepository)
                .findByExternalIdAndCardExternalId(passengerInfoExternalId, cardExternalId);

        cardService.deletePassengerCard(passengerInfoExternalId, cardExternalId);

        verify(passengerInfoRepository).findByExternalIdAndCardExternalId(passengerInfoExternalId, cardExternalId);
    }

    @Test
    void deletePassengerCard_shouldThrowEntityNotFoundException(){
        UUID passengerInfoExternalId = DataUtil.PASSENGER_INFO_EXTERNAL_ID;
        UUID cardExternalId = DataUtil.CARD_EXTERNAL_ID;

        doReturn(Optional.empty())
                .when(passengerInfoRepository)
                .findByExternalIdAndCardExternalId(passengerInfoExternalId, cardExternalId);

        assertThrows(EntityNotFoundException.class,
                () -> cardService.deletePassengerCard(passengerInfoExternalId, cardExternalId));

        verify(passengerInfoRepository).findByExternalIdAndCardExternalId(eq(passengerInfoExternalId), eq(cardExternalId));
    }
}
