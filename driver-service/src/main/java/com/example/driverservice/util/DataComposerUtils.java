package com.example.driverservice.util;

import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.dto.response.AllCarsResponse;
import com.example.driverservice.dto.response.AllDriversResponse;
import com.example.driverservice.mapper.CarMapper;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.projections.CarView;
import com.example.driverservice.model.projections.DriverView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataComposerUtils {

    private final CarMapper carMapper;

    public DriverInfoMessage buildDriverInfoMessage(Driver driver) {
        return DriverInfoMessage.builder()
                .externalId(driver.getExternalId())
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .driverStatus(driver.getDriverStatus())
                .carInfoMessage(carMapper.toMessage(driver.getCar()))
                .build();
    }

    public AllCarsResponse buildAllCarsDto(Page<CarView> allCarsViews) {
        return AllCarsResponse.builder()
                .carViewList(allCarsViews.getContent())
                .currentPageNumber(allCarsViews.getNumber())
                .totalPages(allCarsViews.getTotalPages())
                .totalElements(allCarsViews.getTotalElements())
                .build();
    }

    public AllDriversResponse buildAllDriversDto(Page<DriverView> allDriversViews) {
        return AllDriversResponse.builder()
                .driverViewList(allDriversViews.getContent())
                .currentPageNumber(allDriversViews.getNumber())
                .totalPages(allDriversViews.getTotalPages())
                .totalElements(allDriversViews.getTotalElements())
                .build();
    }
}
