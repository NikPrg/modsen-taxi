package com.example.driverservice.mapper;

import com.example.driverservice.amqp.message.CarInfoMessage;
import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.response.CarResponse;
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
    CarResponse toDto(Car car);

    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    Car toCar(CarRequest carRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCar(UpdateCarRequest carRequestDto, @MappingTarget Car car);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "carLicensePlate", source = "licensePlate")
    @Mapping(target = "carModel", source = "model")
    @Mapping(target = "carColor", source = "color")
    CarInfoMessage toMessage(Car car);
}