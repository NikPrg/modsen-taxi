package com.example.ridesservice.model;

import jakarta.persistence.Embeddable;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Setter
@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CarInfo {

    private String carLicensePlate;

    private String carModel;

    private String carColor;
}
