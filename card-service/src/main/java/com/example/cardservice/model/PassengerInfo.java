package com.example.cardservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.Builder;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "passengers_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "externalId")
@Builder
public class PassengerInfo {

    @Id
    @SequenceGenerator(name = "passenger_info_id_generator", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passenger_info_id_generator")
    private Long id;

    private UUID externalId;

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PassengerCard> cards = new ArrayList<>();

    public void addCard(Card card) {
        PassengerCard passengerCard = new PassengerCard(this, card);
        cards.add(passengerCard);
        card.getPassengers().add(passengerCard);
    }

    public void removeCard(Card card) {
        cards.removeIf(passengerCard -> passengerCard.getPassenger().equals(this)
                && passengerCard.getCard().equals(card));
    }
}
