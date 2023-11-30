package com.example.passengerservice.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter
@Setter
@Builder
public class Discount {
    private Integer discountValueInPercents;

    private LocalDateTime discountCreatedAt;

    private LocalDateTime discountExpiredAt;

    private boolean discountIsUsed;
}
