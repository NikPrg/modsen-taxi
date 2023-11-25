package com.example.driverservice.exception;

public class CarNotBelongDriverException extends RuntimeException{
    public CarNotBelongDriverException(String message) {
        super(message);
    }
}