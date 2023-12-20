package com.example.passengerservice.controller.api;

import com.example.passengerservice.dto.request.ChangePhoneRequest;
import com.example.passengerservice.dto.response.AllPassengersResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.passengerservice.util.ApiRoutesConstants.*;

@RestController
@RequestMapping(PUBLIC_API_V1_PASSENGERS)
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePassengerResponse createPassenger(@RequestBody @Valid PassengerRegistrationRequest passengerDto) {
        return passengerService.signUp(passengerDto);
    }

    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponse findPassengerByExternalId(@PathVariable UUID externalId) {
        return passengerService.findPassengerByExternalId(externalId);
    }

    @GetMapping("{externalId}/paymentMethod")
    @ResponseStatus(HttpStatus.OK)
    public PaymentMethodResponse findPassengerPaymentMethod(@PathVariable UUID externalId) {
        return passengerService.findPassengerPaymentMethod(externalId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public AllPassengersResponse findAllPassengers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return passengerService.findAllPassengers(pageable);
    }

    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public PassengerResponse updatePassenger(@PathVariable UUID externalId,
                                             @RequestBody @Valid PassengerRequest dto) {
        return passengerService.update(externalId, dto);
    }

    @PatchMapping("{externalId}/phone")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassengerPhone(@PathVariable UUID externalId,
                                     @RequestBody @Valid ChangePhoneRequest changePhoneRequest) {
        passengerService.updatePassengerPhone(externalId, changePhoneRequest);
    }

    @PatchMapping("{passengerExternalId}/cards/{cardExternalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCardAsDefaultPaymentMethod(@PathVariable UUID passengerExternalId,
                                              @PathVariable UUID cardExternalId) {
        passengerService.addCardAsDefaultPaymentMethod(passengerExternalId, cardExternalId);
    }

    @PatchMapping("{externalId}/cash")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCashAsDefaultPaymentMethod(@PathVariable UUID externalId) {
        passengerService.addCashAsDefaultPaymentMethod(externalId);
    }

    @DeleteMapping("{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable UUID externalId) {
        passengerService.delete(externalId);
    }
}
