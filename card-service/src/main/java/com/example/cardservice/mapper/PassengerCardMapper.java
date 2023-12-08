package com.example.cardservice.mapper;


import com.example.cardservice.model.PassengerCard;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        uses = CardMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PassengerCardMapper {
//    PassengerCardDto toPassengerCardDto(PassengerCard card);
}
