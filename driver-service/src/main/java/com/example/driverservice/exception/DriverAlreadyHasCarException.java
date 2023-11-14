package com.example.driverservice.exception;

public class DriverAlreadyHasCarException extends RuntimeException{
    public DriverAlreadyHasCarException(String message) {
        super(message);
    }
}