package com.example.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiRoutesConstants {
    public final String PUBLIC_API_V1_PASSENGERS = "/public/api/v1/passengers";
    public final String PASSENGER_EXTERNAL_ID_ENDPOINT = "/{passengerExternalId}";
    public final String PASSENGER_EXTERNAL_ID_PAYMENT_METHOD_ENDPOINT = "/{passengerExternalId}/paymentMethod";
    public final String PASSENGER_EXTERNAL_ID_PHONE_ENDPOINT = "/{passengerExternalId}/phone";
    public final String PASSENGER_EXT_ID_CARDS_CARD_EXT_ID_ENDPOINT = "/{passengerExternalId}/cards/{cardExternalId}";
    public final String PASSENGER_EXTERNAL_ID_CASH_ENDPOINT = "/{passengerExternalId}/cash";
}
