package com.example.passengerservice.dto.response;

import com.example.passengerservice.model.enums.PaymentMethod;
import lombok.Builder;

@Builder
public record PaymentInfoResponse(
        PaymentMethod paymentMethod
) {
}
