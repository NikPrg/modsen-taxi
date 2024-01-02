package com.example.driverservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiRoutesConstants {
    public final String PUBLIC_API_V1 = "/public/api/v1";
    public final String PUBLIC_API_V1_DRIVERS = "/public/api/v1/drivers";
    public final String CARS_ENDPOINT = "/cars";
    public final String DRIVER_EXTERNAL_ID_ENDPOINT = "/{driverExternalId}";
    public final String CARS_CARD_EXTERNAL_ID_ENDPOINT = "/cars/{cardExternalId}";
    public final String DRIVERS_DRIVER_EXTERNAL_ID_CARS_ENDPOINT = "/drivers/{driverExternalId}/cars";
    public final String DRIVERS_DRIVER_EXTERNAL_ID_CARS_CAR_EXTERNAL_ID_ENDPOINT = "/drivers/{driverExternalId}/cars/{carExternalId}";
}