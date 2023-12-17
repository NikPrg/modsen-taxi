package com.example.paymentservice.model;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
