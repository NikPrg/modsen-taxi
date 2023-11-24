package com.example.ridesservice.model;

import com.example.ridesservice.model.enums.DriverStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "drivers_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "externalId")
@Builder
public class DriverInfo {

    @Id
    @SequenceGenerator(name = "driver_info_id_generator", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_info_id_generator")
    private Long id;

    private UUID externalId;

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private DriverStatus driverStatus;

    @Embedded
    private CarInfo carInfo;

    @OneToMany(mappedBy = "driver", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ride> rides = new ArrayList<>();

    public void addRide(Ride ride) {
        this.rides.add(ride);
        ride.setDriver(this);
    }
}
