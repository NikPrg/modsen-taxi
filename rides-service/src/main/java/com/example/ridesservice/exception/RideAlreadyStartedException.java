package com.example.ridesservice.exception;

public class RideAlreadyStartedException extends RuntimeException {
    public RideAlreadyStartedException(String message) {
        super(message);
    }
}
