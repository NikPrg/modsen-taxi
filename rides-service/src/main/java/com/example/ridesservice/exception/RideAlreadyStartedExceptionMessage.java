package com.example.ridesservice.exception;

public class RideAlreadyStartedExceptionMessage extends RuntimeException {
    public RideAlreadyStartedExceptionMessage(String message) {
        super(message);
    }
}
