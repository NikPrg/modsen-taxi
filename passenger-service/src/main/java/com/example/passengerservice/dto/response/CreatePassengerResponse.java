package com.example.passengerservice.dto.response;

import com.example.passengerservice.model.Discount;
import com.example.passengerservice.model.enums.PaymentMethod;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreatePassengerResponse(
        Long id,
        UUID externalId,
        String firstName,
        String lastName,
        String phone,
        Double rate,
        PaymentMethod paymentMethod,
        Discount discount
) {
}
