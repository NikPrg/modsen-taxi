package com.example.driverservice.controller.api;

import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.request.UpdateDriverRequest;
import com.example.driverservice.dto.response.AllDriversResponse;
import com.example.driverservice.dto.response.CreateDriverResponse;
import com.example.driverservice.dto.response.DriverResponse;
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
    public CreateDriverResponse createDriver(@RequestBody @Valid DriverRequest createDriverRequest) {
        return driverService.createDriver(createDriverRequest);
    }

    @GetMapping(DRIVER_EXTERNAL_ID_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public DriverResponse findDriverByExternalId(@PathVariable UUID driverExternalId) {
        return driverService.findDriverByExternalId(driverExternalId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AllDriversResponse findAllDrivers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return driverService.findAllDrivers(pageable);
    }

    @PutMapping(DRIVER_EXTERNAL_ID_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    public DriverResponse updateDriver(@PathVariable UUID driverExternalId,
                                       @RequestBody @Valid UpdateDriverRequest updateDriverRequest) {
        return driverService.updateDriver(driverExternalId, updateDriverRequest);
    }

    @DeleteMapping(DRIVER_EXTERNAL_ID_ENDPOINT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDriver(@PathVariable UUID driverExternalId) {
        driverService.deleteDriver(driverExternalId);
    }

}