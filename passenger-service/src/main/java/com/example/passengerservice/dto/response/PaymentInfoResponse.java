package com.example.passengerservice.dto.response;

import com.example.passengerservice.dto.model.PassengerCardDto;
import com.example.passengerservice.model.PaymentMethod;

import java.util.List;

public record PaymentInfoResponse(
        PaymentMethod paymentMethod,
        List<PassengerCardDto> cards
) {
}
