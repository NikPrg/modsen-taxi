package com.example.driverservice.service.impl;

import com.example.driverservice.amqp.handler.SendRequestHandler;
import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.request.UpdateDriverRequest;
import com.example.driverservice.dto.response.AllDriversResponse;
import com.example.driverservice.dto.response.CreateDriverResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.mapper.DriverMapper;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.enums.DriverStatus;
import com.example.driverservice.model.projections.DriverView;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.util.DataComposerUtils;
import com.example.driverservice.util.DataUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class DriverServiceImplTest {
    private final ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DriverMapper driverMapper;
    @Mock
    private SendRequestHandler sendRequestHandler;
    @Mock
    private DataComposerUtils dataComposerUtils;

    @InjectMocks
    private DriverServiceImpl driverService;


    @Test
    void findDriverByExternalId_shouldReturnDriverResponseWithNoCar() {
        Driver driver = DataUtil.defaultDriverWithNoCarStatus();
        DriverResponse expectedResult = DataUtil.defaultDriverResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doReturn(expectedResult)
                .when(driverMapper)
                .toDto(driver);

        DriverResponse actual = driverService.findDriverByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expectedResult);
        assertNull(actual.car());

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(driverMapper).toDto(eq(driver));
    }

    @Test
    void findDriverByExternalId_shouldReturnDriverResponse() {

        Driver driver = DataUtil.defaultDriverWithCar();
        DriverResponse expectedResult = DataUtil.defaultDriverResponseWithCar();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doReturn(expectedResult)
                .when(driverMapper)
                .toDto(driver);

        DriverResponse actual = driverService.findDriverByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expectedResult);
        assertNotNull(actual.car());

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(driverMapper).toDto(eq(driver));
    }

    @Test
    void findDriverByExternalId_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> driverService.findDriverByExternalId(DataUtil.DRIVER_EXTERNAL_ID));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
    }

    @Test
    void findAllDrivers_shouldReturnExpectedResponse() {
        List<DriverView> driverProjections = List.of(
                projectionFactory.createProjection(DriverView.class),
                projectionFactory.createProjection(DriverView.class),
                projectionFactory.createProjection(DriverView.class)
        );
        Pageable pageable = Pageable.ofSize(3);
        AllDriversResponse expected = new AllDriversResponse(driverProjections, 0, 1, pageable.getPageSize());
        PageImpl<DriverView> driverViews = new PageImpl<>(driverProjections);

        doReturn(driverViews)
                .when(driverRepository)
                .findAllDriversViews(pageable);
        doReturn(expected)
                .when(dataComposerUtils)
                .buildAllDriversDto(driverViews);

        AllDriversResponse actual = driverService.findAllDrivers(pageable);

        assertThat(actual.totalElements()).isEqualTo(3);
        assertThat(actual).isEqualTo(expected);

        verify(driverRepository).findAllDriversViews(eq(pageable));
    }

    @Test
    void findAllDrivers_shouldReturnEmptyResponse() {
        List<DriverView> driverProjections = Collections.emptyList();
        PageImpl<DriverView> driverViews = new PageImpl<>(driverProjections);
        AllDriversResponse emptyResponse = new AllDriversResponse(driverProjections, 0, 0, 0);
        Pageable pageable = Pageable.unpaged();

        doReturn(driverViews)
                .when(driverRepository)
                .findAllDriversViews(pageable);
        doReturn(emptyResponse)
                .when(dataComposerUtils)
                .buildAllDriversDto(driverViews);

        AllDriversResponse actual = driverService.findAllDrivers(pageable);

        assertThat(actual.driverViewList()).isEmpty();
        assertThat(actual.totalElements()).isEqualTo(0);
        assertThat(actual).isEqualTo(emptyResponse);

        verify(driverRepository).findAllDriversViews(eq(pageable));
    }

    @Test
    void createDriver_shouldReturnExpectedResponse() {
        DriverRequest driverRequest = DataUtil.defaultDriverRequest();
        Driver driver = DataUtil.defaultDriverWithCreatedStatus();
        DriverInfoMessage message = DataUtil.defaultDriverInfoMessageWithNoCar();
        CreateDriverResponse expected = DataUtil.defaultCreateDriverResponse();

        doReturn(driver)
                .when(driverMapper)
                .toDriver(driverRequest);
        doReturn(message)
                .when(dataComposerUtils)
                .buildDriverInfoMessage(driver);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverInfoRequestToKafka(message);
        doReturn(expected)
                .when(driverMapper)
                .toCreateDto(driver);

        CreateDriverResponse actual = driverService.createDriver(driverRequest);

        assertThat(DriverStatus.CREATED).isEqualTo(driver.getDriverStatus());
        assertThat(actual).isEqualTo(expected);

        verify(driverMapper).toDriver(eq(driverRequest));
        verify(driverRepository).save(eq(driver));
        verify(dataComposerUtils).buildDriverInfoMessage(eq(driver));
        verify(sendRequestHandler).sendDriverInfoRequestToKafka(eq(message));
        verify(driverMapper).toCreateDto(eq(driver));
    }

    @Test
    void updateDriver_shouldReturnExpectedResponse() {
        Driver storedDriver = DataUtil.defaultDriverWithCreatedStatus();
        UpdateDriverRequest request = DataUtil.defaultUpdateDriverRequest();
        DriverInfoMessage message = DataUtil.defaultDriverInfoMessageWithNoCar();
        DriverResponse expected = DataUtil.defaultDriverResponse();

        doReturn(Optional.of(storedDriver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doNothing()
                .when(driverMapper)
                .updateDriver(request, storedDriver);
        doReturn(message)
                .when(dataComposerUtils)
                .buildDriverInfoMessage(storedDriver);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverInfoRequestToKafka(message);
        doReturn(expected)
                .when(driverMapper)
                .toDto(storedDriver);

        DriverResponse actual = driverService.updateDriver(DataUtil.DRIVER_EXTERNAL_ID, request);

        assertThat(actual).isEqualTo(expected);

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(driverMapper).updateDriver(eq(request), eq(storedDriver));
        verify(dataComposerUtils).buildDriverInfoMessage(eq(storedDriver));
        verify(sendRequestHandler).sendDriverInfoRequestToKafka(eq(message));
        verify(driverMapper).toDto(storedDriver);
    }

    @Test
    void updateDriver_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> driverService.updateDriver(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.defaultUpdateDriverRequest()));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
    }

    @Test
    void deleteDriver_shouldCallDeleteMethod() {
        Driver driver = DataUtil.defaultDriverWithAvailableStatus();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doNothing()
                .when(driverRepository)
                .delete(driver);

        driverService.deleteDriver(DataUtil.DRIVER_EXTERNAL_ID);

        verify(driverRepository).findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        verify(driverRepository).delete(DataUtil.defaultDriverWithCreatedStatus());
    }
}
