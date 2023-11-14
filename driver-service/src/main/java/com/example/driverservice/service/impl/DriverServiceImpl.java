package com.example.driverservice.service.impl;

import com.example.driverservice.dto.request.DriverRequestDto;
import com.example.driverservice.dto.response.AllDriversResponseDto;
import com.example.driverservice.dto.response.CreateDriverResponseDto;
import com.example.driverservice.dto.response.DriverResponseDto;
import com.example.driverservice.mapper.DriverMapper;
import com.example.driverservice.model.projections.DriverView;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.service.DriverService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.driverservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepo;
    private final DriverMapper driverMapper;

    @Transactional
    @Override
    public CreateDriverResponseDto createDriver(DriverRequestDto createDriverRequestDto) {
        var driver = driverMapper.toDriver(createDriverRequestDto);
        driverRepo.save(driver);
        return driverMapper.toCreateDto(driver);
    }

    @Override
    public DriverResponseDto findDriverByExternalId(UUID externalId) {
        var driver = driverRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        return driverMapper.toDto(driver);
    }

    @Transactional(readOnly = true)
    @Override
    public AllDriversResponseDto findAllDrivers(Pageable pageable) {
        Page<DriverView> allDriversViews = driverRepo.findAllDriversViews(pageable);
        return buildAllDriversDto(allDriversViews);
    }

    @Transactional
    @Override
    public DriverResponseDto updateDriver(UUID externalId, DriverRequestDto driverRequestDto) {
        var storedDriver = driverRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        driverMapper.updateDriver(driverRequestDto, storedDriver);
        return driverMapper.toDto(storedDriver);
    }

    @Transactional
    @Override
    public void deleteDriver(UUID externalId) {
        var driver = driverRepo.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(externalId)));
        driverRepo.delete(driver);
    }

    private AllDriversResponseDto buildAllDriversDto(Page<DriverView> allDriversViews) {
        return AllDriversResponseDto.builder()
                .driverViewList(allDriversViews.getContent())
                .currentPageNumber(allDriversViews.getNumber())
                .totalPages(allDriversViews.getTotalPages())
                .totalElements(allDriversViews.getTotalElements())
                .build();
    }
}