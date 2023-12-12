package com.example.cardservice.mapper;

import com.example.cardservice.dto.model.CardDto;
import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.model.Card;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.Builder;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {
    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    Card toCard(CardRegistrationDto cardDto);

    CardDto toCardDto(Card card);
}
