package com.example.cardservice.mapper;


import com.example.cardservice.dto.request.CardRegistrationDto;
import com.example.cardservice.model.Card;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {
//    CardDto toDto(Card card);

    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    Card toCard(CardRegistrationDto cardDto);
}
