package com.example.cardservice.util;

import com.example.cardservice.amqp.message.CardInfoMessage;
import com.example.cardservice.amqp.message.ChangeDefaultPaymentMethodMessage;
import com.example.cardservice.amqp.message.ErrorInfoMessage;
import com.example.cardservice.dto.model.CardDto;
import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.dto.response.AllCardsResponse;
import com.example.cardservice.dto.response.CreateCardResponse;
import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerCard;
import com.example.cardservice.model.PassengerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DataUtil {
    public static final Long CARD_ID = 1L;
    public static final UUID CARD_EXTERNAL_ID = UUID.randomUUID();
    public static final String CARD_NUMBER = "5532332131234421";

    public static final Long PASSENGER_INFO_ID = 1L;
    public static final UUID PASSENGER_INFO_EXTERNAL_ID = UUID.randomUUID();

    public static final String EXCEPTION_MESSAGE = "EXCEPTION MESSAGE";
    public static final UUID NOT_EXISTED_EXTERNAL_ID = UUID.randomUUID();


    public static Card defaultCard() {
        return Card.builder()
                .id(CARD_ID)
                .externalId(CARD_EXTERNAL_ID)
                .number(CARD_NUMBER)
                .build();
    }

    public static PassengerInfo initPassengerInfo() {
        return PassengerInfo.builder()
                .id(PASSENGER_INFO_ID)
                .externalId(PASSENGER_INFO_EXTERNAL_ID)
                .build();
    }

    public static PassengerInfo defaultPassengerInfoWithCard() {
        List<PassengerCard> passengerCards = new ArrayList<>();
        passengerCards.add(new PassengerCard(initPassengerInfo(), defaultCard()));

        return PassengerInfo.builder()
                .id(PASSENGER_INFO_ID)
                .externalId(PASSENGER_INFO_EXTERNAL_ID)
                .cards(passengerCards)
                .build();
    }

    public static CardRegistrationDto defaultCardRegistrationDto() {
        return CardRegistrationDto.builder()
                .number(CARD_NUMBER)
                .build();
    }

    public static CreateCardResponse defaultCreateCardResponse() {
        return CreateCardResponse.builder()
                .cardExternalId(CARD_EXTERNAL_ID)
                .build();
    }

    public static CardInfoMessage defaultCardInfoMessage() {
        return new CardInfoMessage(CARD_EXTERNAL_ID);
    }

    public static CardDto defaultCardDto() {
        return new CardDto(CARD_NUMBER);
    }

    public static AllCardsResponse defaultAllCardsResponse() {
        return new AllCardsResponse(List.of(new CardDto(CARD_NUMBER)));
    }

    public static AllCardsResponse defaultAllCardsResponseEmpty() {
        return new AllCardsResponse(Collections.emptyList());
    }

    public static ChangeDefaultPaymentMethodMessage defaultChangeDefaultPaymentMethodMessage() {
        return new ChangeDefaultPaymentMethodMessage(PASSENGER_INFO_EXTERNAL_ID);
    }

    public static ErrorInfoMessage defaultErrorInfoMessage() {
        return new ErrorInfoMessage(PASSENGER_INFO_EXTERNAL_ID, EXCEPTION_MESSAGE);
    }
}

