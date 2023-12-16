package com.example.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name = "card-balances")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "cardExternalId")
@Builder
public class CardBalance {

    @Id
    @SequenceGenerator(name = "card_balance_id_generator", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_balance_id_generator")
    private Long id;

    private UUID cardExternalId;

    private BigDecimal balance;

    @Version
    private Long version;
}
