package com.example.driverservice.model.projections;

import java.time.LocalDate;
import java.util.UUID;

public interface CarView {

    UUID getExternalId();

    String getLicensePlate();

    String getModel();

    String getColor();

    LocalDate getCreatedAt();
}