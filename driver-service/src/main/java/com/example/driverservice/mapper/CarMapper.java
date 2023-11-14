package com.example.driverservice.mapper;

import com.example.driverservice.dto.request.CarRequestDto;
import com.example.driverservice.dto.response.CarResponseDto;
import com.example.driverservice.model.entity.Car;
import org.mapstruct.*;

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