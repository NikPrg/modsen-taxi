package com.example.passengerservice.mapper;

import com.example.passengerservice.dto.model.CardDto;
import com.example.passengerservice.dto.request.CardRegistrationDto;
import com.example.passengerservice.model.Card;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {
    CardDto toDto(Card card);

    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    Card toCard(CardRegistrationDto cardDto);
}
