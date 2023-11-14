package com.example.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexpConstants {
    public final String PHONE_REGEXP = "^\\+375(17|29|33|44|25)[0-9]{3}[0-9]{2}[0-9]{2}$";

    public final String CARD_REGEXP = "\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}";
}
