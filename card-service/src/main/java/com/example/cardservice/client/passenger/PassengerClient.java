package com.example.cardservice.client.passenger;

import com.example.cardservice.dto.response.PassengerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@FeignClient(name = "${app.api.feign-client.passenger.name}", url = "${app.api.feign-client.passenger.url}")
public interface PassengerClient {

    @GetMapping("${app.api.feign-client.passenger.endpoints.findByExternalId}")
    @ResponseStatus(HttpStatus.OK)
    PassengerResponse findPassengerByExternalId(@PathVariable UUID externalId);

}
