package com.example.passengerservice.dto.response;

import com.example.passengerservice.model.enums.PaymentMethod;

public record PaymentInfoResponse(
        PaymentMethod paymentMethod
) {
}
