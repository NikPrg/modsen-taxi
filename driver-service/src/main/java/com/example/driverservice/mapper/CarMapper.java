package com.example.driverservice.mapper;

import com.example.driverservice.dto.request.CarRequestDto;
import com.example.driverservice.dto.response.CarResponseDto;
import com.example.driverservice.model.entity.Car;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Builder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CarMapper {

    CarResponseDto toDto(Car car);

    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    Car toCar(CarRequestDto carRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCar(CarRequestDto carRequestDto, @MappingTarget Car car);
}