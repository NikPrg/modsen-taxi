package com.example.cardservice.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"passengerId", "cardId"})
public class PassengerCardId implements Serializable {
    private Long passengerId;
    private Long cardId;
}
