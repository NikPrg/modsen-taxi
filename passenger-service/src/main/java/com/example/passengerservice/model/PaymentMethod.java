package com.example.passengerservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum PaymentMethod {
    CARD(),
    CASH();
    private String cardNumber;

    public PaymentMethod setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }
}
