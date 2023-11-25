package com.example.ridesservice.exception;

public class RideNotAcceptedException extends RuntimeException{
    public RideNotAcceptedException(String message) {
        super(message);
    }
}
