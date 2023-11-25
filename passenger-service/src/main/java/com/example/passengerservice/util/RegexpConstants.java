package com.example.passengerservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexpConstants {
    public final String PHONE_REGEXP = "^\\+375(17|29|33|44|25)[0-9]{3}[0-9]{2}[0-9]{2}$";

    public final String CARD_REGEXP = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})$";
}
