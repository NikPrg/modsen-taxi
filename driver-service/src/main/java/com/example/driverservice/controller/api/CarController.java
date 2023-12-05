package com.example.driverservice.controller.api;

import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.response.AllCarsResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

import static com.example.driverservice.util.ApiRoutesConstants.*;

@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @PostMapping("/drivers/{driverExternalId}/cars")
    @ResponseStatus(HttpStatus.OK)
    public CarResponse createCar(@PathVariable UUID driverExternalId,
                                 @RequestBody @Valid CarRequest carRequest) {
        return carService.createCar(driverExternalId, carRequest);
    }

    @GetMapping("/cars/{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public CarResponse findCarByExternalId(@PathVariable UUID externalId) {
        return carService.findByExternalId(externalId);
    }

    @GetMapping("/cars")
    @ResponseStatus(HttpStatus.OK)
    public AllCarsResponse findAllCars(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return carService.findAllCars(pageable);
    }

    @PutMapping("/drivers/{driverExternalId}/cars")
    @ResponseStatus(HttpStatus.OK)
    public CarResponse updateDriverCar(@PathVariable UUID driverExternalId,
                                       @RequestBody @Valid UpdateCarRequest updateCarRequest) {
        return carService.updateDriverCar(driverExternalId, updateCarRequest);
    }

    @DeleteMapping("/drivers/{driverExternalId}/cars/{carExternalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriverCar(@PathVariable UUID driverExternalId,
                                @PathVariable UUID carExternalId) {
        carService.deleteDriverCar(driverExternalId, carExternalId);
    }

}