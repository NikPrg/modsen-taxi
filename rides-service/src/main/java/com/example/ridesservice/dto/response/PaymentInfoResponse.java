package com.example.ridesservice.dto.response;

import com.example.ridesservice.model.enums.PaymentMethod;

public record PaymentInfoResponse(
        PaymentMethod paymentMethod
) {
}
