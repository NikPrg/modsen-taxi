package com.example.ridesservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiRoutesConstants {
    public final String PUBLIC_API_V1_RIDES = "/public/api/v1/rides";
    public final String RIDE_EXTERNAL_ID_PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT = "/{rideExternalId}/passengers/{passengerExternalId}";
    public final String PASSENGERS_PASSENGER_EXTERNAL_ID_ENDPOINT = "/passengers/{passengerExternalId}";
    public final String RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_ACCEPT_ENDPOINT = "/{rideExternalId}/drivers/{driverExternalId}/accept";
    public final String RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_START_ENDPOINT = "/{rideExternalId}/drivers/{driverExternalId}/start";
    public final String RIDE_EXTERNAL_ID_DRIVERS_DRIVER_EXTERNAL_ID_FINISH_ENDPOINT = "/{rideExternalId}/drivers/{driverExternalId}/finish";
}

