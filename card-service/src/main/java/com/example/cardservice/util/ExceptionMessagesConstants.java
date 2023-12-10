package com.example.cardservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String CARD_ALREADY_EXIST_PASSENGER_EXCEPTION_MESSAGE =
            "Card already exist for this passenger = %s";
    public final String PASSENGER_WITH_SPECIFIED_CARD_NOT_FOUND_EXCEPTION_MESSAGE =
            "Passenger with externalId = %s, has no card with externalId = %s";
    public final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE =
            "Passenger was not found by given externalId = %s";

}
