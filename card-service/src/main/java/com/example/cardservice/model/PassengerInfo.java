package com.example.cardservice.model;

import jakarta.persistence.*;
import lombok.*;

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
