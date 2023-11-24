package com.example.ridesservice.mapper;

import com.example.ridesservice.dto.response.DriverInfoResponse;
import com.example.ridesservice.model.DriverInfo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DriverInfoMapper {
    DriverInfoResponse toDto(DriverInfo driverInfo);
}
