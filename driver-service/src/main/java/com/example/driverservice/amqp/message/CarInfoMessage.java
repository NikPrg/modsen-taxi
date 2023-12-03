package com.example.driverservice.amqp.message;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CarInfoMessage(
        String carLicensePlate,
        String carModel,
        String carColor
) implements Serializable {
}
