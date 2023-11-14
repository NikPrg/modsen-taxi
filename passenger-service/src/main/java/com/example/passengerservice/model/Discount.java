package com.example.passengerservice.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Getter @Setter
@Builder
public class Discount {
    private Integer discountValueInPercents;

    private LocalDateTime discountCreatedAt;

    private LocalDateTime discountExpiredAt;

    private boolean discountIsUsed;
}
