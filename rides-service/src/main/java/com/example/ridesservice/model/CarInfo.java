package com.example.ridesservice.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Embeddable
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarInfo {

    private String licensePlate;

    private String model;

    private String color;
}
