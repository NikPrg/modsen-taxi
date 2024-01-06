package com.example.e2eservice.feign.client;

import com.example.e2eservice.feign.response.driver.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@FeignClient(name = "${app.api.feign.clients.driver.name}", url = "${app.api.feign.clients.driver.base-url}")
public interface DriverClient {

    @GetMapping("${app.api.feign.clients.driver.routes.find-driver-by-external-id}")
    @ResponseStatus(HttpStatus.OK)
    DriverResponse findDriverByExternalId(@PathVariable UUID driverExternalId);
}