package com.example.passengerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum PaymentMethod {
    CARD(),
    CASH();
    private String cardNumber;

    public PaymentMethod setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }
}
