package com.example.ridesservice.mapper;

import com.example.ridesservice.amqp.message.DriverInfoMessage;
import com.example.ridesservice.dto.response.DriverInfoResponse;
import com.example.ridesservice.model.DriverInfo;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DriverInfoMapper {
    DriverInfoResponse toDto(DriverInfo driverInfo);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateDriver(DriverInfoMessage driverInfoMessage, @MappingTarget DriverInfo driver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DriverInfo toDriver(DriverInfoMessage driverInfoMessage);
}
