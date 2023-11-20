package com.example.ridesservice.model;

import com.example.ridesservice.model.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "drivers_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "driverExternalId")
@Builder
public class DriverInfo {

    @Id
    @SequenceGenerator(name = "driver_info_generator", allocationSize = 5)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_info_generator")
    private Long id;

    private UUID driverExternalId;

//    @Embedded
//    private CarInfo carInfo;

    @Enumerated(EnumType.STRING)
    private DriverStatus driverStatus;

    @OneToMany(mappedBy = "driver", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Ride> rides = new ArrayList<>();

    public void addRide(Ride ride){
        this.rides.add(ride);
        ride.setDriver(this);
    }
}
