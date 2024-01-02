package com.example.cardservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiRoutesConstants {
    public final String PUBLIC_API_V1_PASSENGERS = "/public/api/v1/passengers";
    public final String PASSENGER_EXTERNAL_ID_CARDS = "/{passengerExternalId}/cards";
    public final String PASSENGER_EXTERNAL_ID_CARDS_DEFAULT = "/{passengerExternalId}/cards/default";
    public final String PASSENGER_EXTERNAL_ID_CARDS_CARD_EXTERNAL_ID = "/{passengerExternalId}/cards/{cardExternalId}";
}
