package com.example.cardservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String CARD_NOT_FOUND_EXCEPTION_MESSAGE = "Card was not found by given externalId = %s";

    public final String CARD_ALREADY_EXIST_PASSENGER_EXCEPTION_MESSAGE = "Card already exist for this passenger = %s";

    public final String PASSENGER_WITH_SPECIFIED_CARD_NOT_FOUND_EXCEPTION_MESSAGE =
            "Passenger with externalId = %s, has no card with externalId = %s";

    public final String CARD_NOT_BELONG_PASSENGER_EXCEPTION_MESSAGE =
            "Card with externalId = %s, does not belong to the passenger with externalId = %s";

    public final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE = "Passenger was not found by given externalId = %s";
    public final String PASSENGER_HAS_NO_CARDS_EXCEPTION_MESSAGE = "Passenger with given externalId = %s has no cards";

}
