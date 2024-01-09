package com.example.e2eservice.entity;

public enum DriverStatus {
    CREATED, //same as NO_CAR, used for Spring Integration logic
    NO_CAR, //to prevent the driver from being able to take a ride if he doesn't have a car.
    TOWARDS_PASSENGER,
    AVAILABLE,
    UNAVAILABLE,
    DELETED //to prevent tge driver from being able to take a ride if it is deleted
}
