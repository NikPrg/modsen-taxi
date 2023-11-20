package com.example.driverservice.controller.api;

import com.example.driverservice.dto.request.DriverRequestDto;
import com.example.driverservice.dto.response.AllDriversResponseDto;
import com.example.driverservice.dto.response.CreateDriverResponseDto;
import com.example.driverservice.dto.response.DriverResponseDto;
import com.example.driverservice.service.DriverService;
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
@RequestMapping(PUBLIC_API_V1_DRIVERS)
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateDriverResponseDto createDriver(@RequestBody @Valid DriverRequestDto createDriverRequestDto) {
        return driverService.createDriver(createDriverRequestDto);
    }

    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public DriverResponseDto findDriverByExternalId(@PathVariable UUID externalId) {
        return driverService.findDriverByExternalId(externalId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AllDriversResponseDto findAllDrivers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return driverService.findAllDrivers(pageable);
    }

    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public DriverResponseDto updateDriver(@PathVariable UUID externalId,
                                          @RequestBody @Valid DriverRequestDto driverRequestDto) {
        return driverService.updateDriver(externalId, driverRequestDto);
    }

    @DeleteMapping("{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable UUID externalId) {
        driverService.deleteDriver(externalId);
    }

}