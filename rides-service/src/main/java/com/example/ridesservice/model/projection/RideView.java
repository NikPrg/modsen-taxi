package com.example.ridesservice.model.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RideView {

    UUID getExternalId();

    String getPickUpAddress();

    String getDestinationAddress();

    Double getRideCost();

    Long getRideDuration();

    LocalDateTime getRideCreatedAt();

}
