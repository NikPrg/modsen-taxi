package com.example.paymentservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionMessagesConstants {
    public final String CARD_BALANCE_NOT_FOUND_ERROR_MESSAGE =
            "Card balance was not found by given externalId = %s";
}
