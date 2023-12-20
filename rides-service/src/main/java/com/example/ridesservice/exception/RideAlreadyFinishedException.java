package com.example.ridesservice.exception;

public class RideAlreadyFinishedException extends RuntimeException{
    public RideAlreadyFinishedException(String message) {
        super(message);
    }
}
