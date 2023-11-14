package com.example.passengerservice.model.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.UUID;

public interface PassengerView {
    UUID getExternalId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

    String getPhone();

    Double getRate();

    LocalDate getCreatedAt();
}
