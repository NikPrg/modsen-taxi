package com.example.cardservice.mapper;

import com.example.cardservice.dto.response.PassengerResponse;
import com.example.cardservice.model.PassengerInfo;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PassengerInfoMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    PassengerInfo toPassengerInfo(PassengerResponse passengerResponse);

}
