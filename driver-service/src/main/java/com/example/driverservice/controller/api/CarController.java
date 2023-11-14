package com.example.driverservice.controller.api;

import com.example.driverservice.dto.request.CarRequestDto;
import com.example.driverservice.dto.response.AllCarsResponseDto;
import com.example.driverservice.dto.response.CarResponseDto;
import com.example.driverservice.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.driverservice.util.ApiRoutesConstants.*;

@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping("/drivers/{driverExternalId}/cars")
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto createCar(@PathVariable UUID driverExternalId,
                                    @RequestBody @Valid CarRequestDto carRequest) {
        return carService.createCar(driverExternalId, carRequest);
    }

    @GetMapping("/cars/{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto findCarByExternalId(@PathVariable UUID externalId) {
        return carService.findByExternalId(externalId);
    }

    @GetMapping("/cars")
    @ResponseStatus(HttpStatus.OK)
    public AllCarsResponseDto findAllCars(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return carService.findAllCars(pageable);
    }

    @PutMapping("/drivers/{driverExternalId}/cars")
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto updateDriverCar(@PathVariable UUID driverExternalId,
                                          @RequestBody @Valid CarRequestDto carRequestDto) {
        return carService.updateDriverCar(driverExternalId, carRequestDto);
    }

    @DeleteMapping("/drivers/{driverExternalId}/cars/{carExternalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriverCar(@PathVariable UUID driverExternalId,
                                @PathVariable UUID carExternalId){
        carService.deleteDriverCar(driverExternalId, carExternalId);
    }

}