package com.example.driverservice.mapper;

import com.example.driverservice.dto.request.DriverRequestDto;
import com.example.driverservice.dto.response.CreateDriverResponseDto;
import com.example.driverservice.dto.response.DriverResponseDto;
import com.example.driverservice.model.entity.Driver;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MappingConstants;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Builder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        uses = {CarMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DriverMapper {
    CreateDriverResponseDto toCreateDto(Driver driver);

    @Mapping(target = "externalId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "rate", constant = "5.0")
    Driver toDriver(DriverRequestDto createDriverRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDriver(DriverRequestDto driverRequestDto, @MappingTarget Driver storedDriver);

    DriverResponseDto toDto(Driver driver);
}