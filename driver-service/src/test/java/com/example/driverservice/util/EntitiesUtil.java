package com.example.driverservice.util;

import com.example.driverservice.model.entity.Car;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.enums.DriverStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class EntitiesUtil {
    public static Long VLAD_ID = 11L;
    public static UUID VLAD_EXTERNAL_ID = UUID.fromString("54bb2de9-c518-488a-9898-015faa6dee3c");
    public static String VLAD_FIRST_NAME = "Vlad";
    public static String VLAD_LAST_NAME = "Vladovich";
    public static String VLAD_PHONE = "+375259422972";
    public static Double VLAD_RATE = 5.0;
    public static LocalDateTime VLAD_CREATED_AT = LocalDateTime.of(2023, 11, 12, 11, 30, 30);
    public static DriverStatus VLAD_STATUS = DriverStatus.AVAILABLE;

    public static Long IVAN_ID = 13L;
    public static UUID IVAN_EXTERNAL_ID = UUID.fromString("877019e9-1cee-4c22-b612-e49306dd7e4d");

    public static Long V_CAR_ID = 11L;
    public static UUID V_CAR_EXTERNAL_ID = UUID.fromString("2952848e-0da5-44c2-a359-d96736466bb0");
    public static Long V_CAR_DRIVER_ID = VLAD_ID;
    public static String V_CAR_LICENSE_PLATE = "8628AX-3";
    public static String V_CAR_MODEL = "Honda Civic";
    public static String V_CAR_COLOR = "white";
    public static LocalDateTime V_CAR_CREATED_AT = LocalDateTime.of(2023, 12, 12, 10, 30, 30);

    public static Long G_CAR_ID = 12L;
    public static UUID G_CAR_EXTERNAL_ID = UUID.fromString("7eb73b09-adc4-4662-b6a1-e81f45bce426");

    public static Driver vladAvailableDriver(){
        return new Driver(VLAD_ID, VLAD_EXTERNAL_ID, VLAD_FIRST_NAME, VLAD_LAST_NAME, VLAD_PHONE, VLAD_RATE, VLAD_STATUS, VLAD_CREATED_AT, initVladCar());
    }

    public static Car vCar(){
        return new Car(V_CAR_ID, V_CAR_EXTERNAL_ID, V_CAR_LICENSE_PLATE, V_CAR_MODEL, V_CAR_COLOR, V_CAR_CREATED_AT, initVlad());
    }

    public static Car initVladCar(){
        return new Car(V_CAR_ID, V_CAR_EXTERNAL_ID, V_CAR_LICENSE_PLATE, V_CAR_MODEL, V_CAR_COLOR, V_CAR_CREATED_AT, null);
    }

    public static Driver initVlad(){
        return new Driver(VLAD_ID, VLAD_EXTERNAL_ID, VLAD_FIRST_NAME, VLAD_LAST_NAME, VLAD_PHONE, VLAD_RATE, VLAD_STATUS, VLAD_CREATED_AT, null);
    }
}
