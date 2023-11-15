package com.example.passengerservice.exception;

public class CardNotBelongPassengerException extends RuntimeException{
    public CardNotBelongPassengerException(String message) {
        super(message);
    }
}
