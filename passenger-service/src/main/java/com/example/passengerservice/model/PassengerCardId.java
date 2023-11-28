package com.example.passengerservice.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
