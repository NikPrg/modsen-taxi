package com.example.ridesservice.feign.client;

import com.example.ridesservice.feign.response.DefaultCardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "${app.api.feign.clients.card.name}", url = "${app.api.feign.clients.card.base-url}")
public interface CardClient {

    @GetMapping("${app.api.feign.clients.card.routes.get-default-card}")
    DefaultCardResponse findDefaultCardByPassengerExternalId(@PathVariable UUID passengerExternalId);
}
