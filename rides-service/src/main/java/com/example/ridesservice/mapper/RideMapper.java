package com.example.ridesservice.mapper;

import com.example.ridesservice.dto.request.CreateRideRequest;

import com.example.ridesservice.dto.response.ride.AcceptRideResponse;
import com.example.ridesservice.dto.response.ride.CreateRideResponse;
import com.example.ridesservice.dto.response.ride.FinishRideResponse;
import com.example.ridesservice.dto.response.ride.GetRideResponse;
import com.example.ridesservice.dto.response.ride.StartRideResponse;
import com.example.ridesservice.model.DriverInfo;
import com.example.ridesservice.model.Ride;
import com.example.ridesservice.model.enums.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Builder;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        uses = {DriverInfoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RideMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GetRideResponse toGetRideDto(Ride ride);

    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "rideStatus", constant = "INITIATED")
    Ride toRide(CreateRideRequest createRideRequestDto, UUID passengerExternalId, double rideCost);

    CreateRideResponse toCreateRideDto(Ride ride);

    AcceptRideResponse toAcceptRideDto(Ride ride);

    StartRideResponse toStartRideDto(Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "rideStatus", constant = "ACCEPTED")
    void updateRideOnAcceptance(DriverInfo driver, @MappingTarget Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "rideStatus", constant = "FINISHED")
    @Mapping(target = "rideDuration", expression = "java(java.time.temporal.ChronoUnit.MINUTES.between(ride.getRideStartedAt(), java.time.LocalTime.now()))")
    void updateRideOnFinish(PaymentMethod paymentMethod, @MappingTarget Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    FinishRideResponse toFinishRideResponse(Ride ride);
}
