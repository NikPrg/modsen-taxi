package com.example.driverservice.exception;

public class DriverCarNotFoundException extends RuntimeException{
    public DriverCarNotFoundException(String message) {
        super(message);
    }
}