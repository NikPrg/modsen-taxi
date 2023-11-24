package com.example.ridesservice.exception;

public class PassengerRideNotFoundException extends RuntimeException{
    public PassengerRideNotFoundException(String message) {
        super(message);
    }
}
