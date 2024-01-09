package com.example.passengerservice.util;

import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.model.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.UUID;

public class EntitiesUtil {
    public static final Long NIKITA_ID = 1L;
    public static final UUID NIKITA_EXTERNAL_ID = UUID.fromString("55bb3530-96b7-4adb-a9a6-c9062439fed8");
    public static final String NIKITA_FIRST_NAME = "Nikita";
    public static final String NIKITA_LAST_NAME = "Rodriguez";
    public static final String NIKITA_PHONE = "+375252412972";
    public static final double NIKITA_RATE = 5.0;
    public static final PaymentMethod NIKITA_PAYMENT_METHOD = PaymentMethod.CASH;
    public static final LocalDateTime NIKITA_CREATED_AT = LocalDateTime.of(2023, 11, 12, 18, 30, 30);

    public static final Long SAVELIY_ID = 2L;
    public static final UUID SAVELIY_EXTERNAL_ID = UUID.fromString("eea59cd6-0c9a-48de-8f17-263b496d1a5f");
    public static final String SAVELIY_FIRST_NAME = "Saveliy";
    public static final String SAVELIY_LAST_NAME = "Bennett";
    public static final String SAVELIY_PHONE = "+375254561234";
    public static final double SAVELIY_RATE = 4.5;
    public static final PaymentMethod SAVELIY_PAYMENT_METHOD = PaymentMethod.CARD;
    public static final LocalDateTime SAVELIY_CREATED_AT = LocalDateTime.of(2023, 11, 12, 17, 30, 30);

    public static Passenger nikitaRodriguez() {
        return new Passenger(NIKITA_ID, NIKITA_EXTERNAL_ID, NIKITA_FIRST_NAME, NIKITA_LAST_NAME, NIKITA_PHONE, NIKITA_RATE, NIKITA_PAYMENT_METHOD, null, NIKITA_CREATED_AT);
    }

    public static Passenger saveliyBennett(){
        return new Passenger(SAVELIY_ID, SAVELIY_EXTERNAL_ID, SAVELIY_FIRST_NAME, SAVELIY_LAST_NAME, SAVELIY_PHONE, SAVELIY_RATE, SAVELIY_PAYMENT_METHOD, null, SAVELIY_CREATED_AT);
    }
}
