package com.example.ridesservice.exception;

public class RideAlreadyFinishedExceptionMessage extends RuntimeException{
    public RideAlreadyFinishedExceptionMessage(String message) {
        super(message);
    }
}
