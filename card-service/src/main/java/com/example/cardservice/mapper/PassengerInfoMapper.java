package com.example.cardservice.mapper;

import com.example.cardservice.amqp.message.NewPassengerInfoMessage;
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
    @Mapping(target = "externalId", source = "passengerExternalId")
    PassengerInfo toPassengerInfo(NewPassengerInfoMessage message);

}
