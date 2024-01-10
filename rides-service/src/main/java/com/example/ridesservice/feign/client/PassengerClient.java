package com.example.ridesservice.feign.client;

import com.example.ridesservice.feign.response.PaymentMethodResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "${app.api.feign.clients.passenger.name}")
public interface PassengerClient {
    @GetMapping("${app.api.feign.clients.passenger.routes.get-payment-method}")
    PaymentMethodResponse findPassengerPaymentMethod(@PathVariable UUID externalId);
}
