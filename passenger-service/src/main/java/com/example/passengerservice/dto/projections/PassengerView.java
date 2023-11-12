package com.example.passengerservice.dto.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.UUID;

public interface PassengerView {
    UUID getId();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

    String getPhone();

    Double getRate();

    LocalDate getCreatedAt();
}
