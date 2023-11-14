package com.example.passengerservice.controller.api;

import com.example.passengerservice.model.projections.PassengerView;
import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponseDto;
import com.example.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.passengerservice.util.HttpConstants.*;


@RestController
@RequestMapping(PUBLIC_API_V1)
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePassengerResponse createPassenger(@RequestBody @Valid PassengerRegistrationDto passengerDto) {
        return passengerService.signUp(passengerDto);
    }

    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponseDto findPassengerByExternalId(@PathVariable UUID externalId) {
        return passengerService.findPassengerByExternalId(externalId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PassengerView> findAllPassengers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return passengerService.findAllPassengers(pageable);
    }

    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponseDto updatePassenger(@PathVariable UUID externalId,
                                                @RequestBody @Valid PassengerRequestDto dto) {
        return passengerService.update(externalId, dto);
    }

    @PatchMapping("{passengerId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public void addCardAsDefaultPaymentMethod(@PathVariable UUID passengerId,
                                              @PathVariable UUID cardId) {
        passengerService.addCardAsDefaultPaymentMethod(passengerId, cardId);
    }

    @PatchMapping("{passengerId}/cash")
    @ResponseStatus(HttpStatus.OK)
    public void addCashAsDefaultPaymentMethod(@PathVariable UUID passengerId) {
        passengerService.addCashAsDefaultPaymentMethod(passengerId);
    }

    @DeleteMapping("{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable UUID externalId) {
        passengerService.delete(externalId);
    }
}
