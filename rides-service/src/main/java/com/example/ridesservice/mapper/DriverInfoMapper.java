package com.example.ridesservice.mapper;

import com.example.ridesservice.amqp.message.DriverInfoMessage;
import com.example.ridesservice.dto.response.DriverInfoResponse;
import com.example.ridesservice.model.DriverInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DriverInfoMapper {
    DriverInfoResponse toDto(DriverInfo driverInfo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DriverInfo toDriver(DriverInfoMessage driverInfoMessage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "carInfo.carModel", source = "carInfoMessage.carModel")
    @Mapping(target = "carInfo.carColor", source = "carInfoMessage.carColor")
    @Mapping(target = "carInfo.carLicensePlate", source = "carInfoMessage.carLicensePlate")
    void updateDriver(DriverInfoMessage payload, @MappingTarget DriverInfo driver);
}
