package com.example.ridesservice.exception;

public class RideNotStartedException extends RuntimeException{
    public RideNotStartedException(String message) {
        super(message);
    }
}
