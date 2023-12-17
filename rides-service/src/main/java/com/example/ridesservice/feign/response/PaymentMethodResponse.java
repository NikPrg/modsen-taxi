package com.example.ridesservice.feign.response;

import com.example.ridesservice.model.enums.PaymentMethod;

public record PaymentMethodResponse(
        PaymentMethod paymentMethod
) {
}
