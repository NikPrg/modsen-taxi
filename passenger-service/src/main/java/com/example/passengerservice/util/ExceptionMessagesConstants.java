package com.example.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE = "Passenger was not found by given externalId = %s";

    public final String CARD_NOT_FOUND_EXCEPTION_MESSAGE = "Card was not found by given externalId = %s";

    public final String CARD_ALREADY_EXIST_EXCEPTION_MESSAGE = "Card already exist for this passenger = %s";

    public final String CARD_NOT_BELONG_PASSENGER_EXCEPTION_MESSAGE =
            "Card with externalId = %s, does not belong to the passenger with externalId = %s";

    public final String USER_WITH_THE_SAME_PHONE_IS_EXISTS_MESSAGE =
            "User with similar phone = %s is already exists";
}
