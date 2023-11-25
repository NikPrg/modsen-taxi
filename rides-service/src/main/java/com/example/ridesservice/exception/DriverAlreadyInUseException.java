package com.example.ridesservice.exception;

public class DriverAlreadyInUseException extends RuntimeException{
    public DriverAlreadyInUseException(String message) {
        super(message);
    }
}
