package com.example.ridesservice.exception;

public class RideNotStartedExceptionMessage extends RuntimeException{
    public RideNotStartedExceptionMessage(String message) {
        super(message);
    }
}
