package com.example.passengerservice.mapper;

import com.example.passengerservice.dto.request.PassengerRegistrationDto;
import com.example.passengerservice.dto.request.PassengerRequestDto;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponseDto;
import com.example.passengerservice.dto.response.PaymentInfoResponse;
import com.example.passengerservice.model.Passenger;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        uses = {CardMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PassengerMapper {
    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "rate", constant = "5.0")
    @Mapping(target = "defaultPaymentMethod", constant = "CASH")
    Passenger toPassenger(PassengerRegistrationDto passengerDto);

    PassengerResponseDto toDto(Passenger passenger);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Passenger updatePassenger(PassengerRequestDto source, @MappingTarget Passenger target);

    @Mapping(target = "paymentMethod", source = "defaultPaymentMethod")
    CreatePassengerResponse toCreateDto(Passenger passenger);

    @Mapping(target = "paymentMethod", source = "defaultPaymentMethod")
    PaymentInfoResponse toPaymentInfoDto(Passenger passenger);
}
