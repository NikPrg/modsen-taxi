package com.example.ridesservice.exception;

public class DriverNotBelongRideException extends RuntimeException{
    public DriverNotBelongRideException(String message) {
        super(message);
    }
}
