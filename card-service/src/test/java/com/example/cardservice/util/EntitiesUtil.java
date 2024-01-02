package com.example.cardservice.util;

import com.example.cardservice.model.Card;
import com.example.cardservice.model.PassengerCard;
import com.example.cardservice.model.PassengerInfo;

import java.util.List;
import java.util.UUID;

public class EntitiesUtil {
    public static final Long NIKITA_ID = 1L;
    public static final UUID NIKITA_EXTERNAL_ID = UUID.fromString("55bb3530-96b7-4adb-a9a6-c9062439fed8");

    public static final Long SAVELIY_ID = 2L;
    public static final UUID SAVELIY_EXTERNAL_ID = UUID.fromString("eea59cd6-0c9a-48de-8f17-263b496d1a5f");

    public static final Long EUGEN_ID = 5L;
    public static final UUID EUGEN_EXTERNAL_ID = UUID.fromString("2e68c5fc-e524-4dd3-9793-0111d3f86750");

    public static final Long A_CARD_ID = 1L;
    public static final UUID A_CARD_EXTERNAL_ID = UUID.fromString("0741a1b0-f7ab-45de-bc83-5c915c254741");
    public static final String A_CARD_NUMBER = "5532332131234421";

    public static final Long B_CARD_ID = 2L;
    public static final UUID B_CARD_EXTERNAL_ID = UUID.fromString("9c278f7b-dacc-4f7c-8526-f309682216fa");
    public static final String B_CARD_NUMBER = "5532332231234421";

    public static PassengerInfo nikitaInfo() {
        return new PassengerInfo(NIKITA_ID, NIKITA_EXTERNAL_ID, List.of(new PassengerCard(initPassenger(NIKITA_ID, NIKITA_EXTERNAL_ID), aCard())));
    }

    public static PassengerInfo saveliyInfo() {
        return new PassengerInfo(SAVELIY_ID, SAVELIY_EXTERNAL_ID, List.of(new PassengerCard(initPassenger(SAVELIY_ID, SAVELIY_EXTERNAL_ID), bCard())));
    }

    public static PassengerInfo eugenInfo() {
        return new PassengerInfo(EUGEN_ID, EUGEN_EXTERNAL_ID, null);
    }

    public static Card aCard() {
        return new Card(A_CARD_ID, A_CARD_EXTERNAL_ID, A_CARD_NUMBER, null);
    }

    public static Card bCard() {
        return new Card(B_CARD_ID, B_CARD_EXTERNAL_ID, B_CARD_NUMBER, null);
    }

    public static PassengerInfo initPassenger(Long id, UUID passengerExternalId) {
        return new PassengerInfo(id, passengerExternalId, null);
    }
}
