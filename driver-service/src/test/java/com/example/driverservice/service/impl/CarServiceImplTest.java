package com.example.driverservice.service.impl;

import com.example.driverservice.amqp.handler.SendRequestHandler;
import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.response.AllCarsResponse;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.exception.CarNotBelongDriverException;
import com.example.driverservice.exception.DriverAlreadyHasCarException;
import com.example.driverservice.exception.DriverCarNotFoundException;
import com.example.driverservice.mapper.CarMapper;
import com.example.driverservice.model.entity.Car;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.projections.CarView;
import com.example.driverservice.repository.CarRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {
    private final ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

    @Mock
    private CarRepository carRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private SendRequestHandler sendRequestHandler;
    @Mock
    private DataComposerUtils dataComposerUtils;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void createCar_shouldReturnExpectedResponse() {
        Car car = DataUtil.defaultCar();
        Driver driver = DataUtil.defaultDriverWithNoCarStatus();
        CarRequest request = DataUtil.defaultCarRequest();
        DriverInfoMessage message = DataUtil.defaultDriverInfoMessageWithCar();
        CarResponse expected = DataUtil.defaultCarResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doReturn(car)
                .when(carMapper)
                .toCar(request);
        doReturn(message)
                .when(dataComposerUtils)
                .buildDriverInfoMessage(driver);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverInfoRequestToKafka(message);
        doReturn(expected)
                .when(carMapper)
                .toDto(car);

        CarResponse actual = carService.createCar(DataUtil.DRIVER_EXTERNAL_ID, request);

        assertThat(actual).isEqualTo(expected);

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(carMapper).toCar(eq(request));
        verify(sendRequestHandler).sendDriverInfoRequestToKafka(eq(message));
        verify(carMapper).toDto(eq(car));
    }

    @Test
    void createCar_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> carService.createCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.defaultCarRequest()));

        verify(driverRepository).findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
    }

    @Test
    void createCar_shouldThrowDriverAlreadyHasCarException() {
        doReturn(Optional.of(DataUtil.defaultDriverWithCar()))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThrows(DriverAlreadyHasCarException.class,
                () -> carService.createCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.defaultCarRequest()));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
    }

    @Test
    void findByExternalId_shouldReturnExpectedResponse() {
        Car car = DataUtil.defaultCar();
        CarResponse expected = DataUtil.defaultCarResponse();

        doReturn(Optional.of(car))
                .when(carRepository)
                .findByExternalId(DataUtil.CAR_EXTERNAL_ID);
        doReturn(expected)
                .when(carMapper)
                .toDto(car);

        CarResponse actual = carService.findByExternalId(DataUtil.CAR_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);

        verify(carRepository).findByExternalId(eq(DataUtil.CAR_EXTERNAL_ID));
        verify(carMapper).toDto(eq(car));
    }

    @Test
    void findByExternalId_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(carRepository)
                .findByExternalId(DataUtil.CAR_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> carService.findByExternalId(DataUtil.CAR_EXTERNAL_ID));

        verify(carRepository).findByExternalId(eq(DataUtil.CAR_EXTERNAL_ID));
    }

    @Test
    void findAllCars_shouldReturnExpectedResponse() {
        List<CarView> carProjections = List.of(
                projectionFactory.createProjection(CarView.class),
                projectionFactory.createProjection(CarView.class),
                projectionFactory.createProjection(CarView.class)
        );
        Pageable pageable = Pageable.ofSize(3);
        AllCarsResponse expected = new AllCarsResponse(carProjections, 0, 1, pageable.getPageSize());
        PageImpl<CarView> carViews = new PageImpl<>(carProjections);

        doReturn(carViews)
                .when(carRepository)
                .findAllCarsViews(pageable);
        doReturn(expected)
                .when(dataComposerUtils)
                .buildAllCarsDto(carViews);

        AllCarsResponse actual = carService.findAllCars(pageable);

        assertThat(actual.totalElements()).isEqualTo(3);
        assertThat(actual).isEqualTo(expected);

        verify(carRepository).findAllCarsViews(eq(pageable));
    }

    @Test
    void findAllCars_shouldReturnEmptyResponse() {
        List<CarView> carProjections = Collections.emptyList();
        PageImpl<CarView> carViews = new PageImpl<>(carProjections);
        AllCarsResponse emptyResponse = new AllCarsResponse(carProjections, 0, 0, 0);
        Pageable pageable = Pageable.unpaged();

        doReturn(carViews)
                .when(carRepository)
                .findAllCarsViews(pageable);
        doReturn(emptyResponse)
                .when(dataComposerUtils)
                .buildAllCarsDto(carViews);

        AllCarsResponse actual = carService.findAllCars(pageable);

        assertThat(actual).isEqualTo(emptyResponse);

        verify(carRepository).findAllCarsViews(eq(pageable));
    }

    @Test
    void updateDriverCar_shouldReturnExpectedResponse() {
        Driver driver = DataUtil.defaultDriverWithCar();
        Car car = driver.getCar();
        DriverInfoMessage message = DataUtil.defaultDriverInfoMessageWithCar();
        UpdateCarRequest request = DataUtil.defaultUpdateCarRequest();
        CarResponse expected = DataUtil.defaultCarResponse();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doNothing()
                .when(carMapper)
                .updateCar(request, car);
        doReturn(message)
                .when(dataComposerUtils)
                .buildDriverInfoMessage(driver);
        doNothing()
                .when(sendRequestHandler)
                .sendDriverInfoRequestToKafka(message);
        doReturn(expected)
                .when(carMapper)
                .toDto(car);

        CarResponse actual = carService.updateDriverCar(DataUtil.DRIVER_EXTERNAL_ID, request);

        assertThat(actual).isEqualTo(expected);

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(carMapper).updateCar(eq(request), eq(car));
        verify(dataComposerUtils).buildDriverInfoMessage(eq(driver));
        verify(sendRequestHandler).sendDriverInfoRequestToKafka(eq(message));
        verify(carMapper).toDto(eq(car));
    }

    @Test
    void updateDriverCar_shouldThrowEntityNotFoundException() {
        doReturn(Optional.empty())
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> carService.updateDriverCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.defaultUpdateCarRequest()));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
    }

    @Test
    void updateDriverCar_shouldThrowDriverCarNotFoundException() {
        Driver driver = DataUtil.defaultDriverWithNoCarStatus();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);

        assertThrows(DriverCarNotFoundException.class,
                () -> carService.updateDriverCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.defaultUpdateCarRequest()));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
    }

    @Test
    void deleteDriverCar_shouldCallDeleteMethod() {
        Car car = DataUtil.defaultCar();
        Driver driver = DataUtil.defaultDriverWithCar();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doReturn(Optional.of(car))
                .when(carRepository)
                .findByExternalId(DataUtil.CAR_EXTERNAL_ID);

        carService.deleteDriverCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.CAR_EXTERNAL_ID);

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(carRepository).findByExternalId(eq(DataUtil.CAR_EXTERNAL_ID));
        verify(carRepository).delete(eq(car));
    }

    @Test
    void deleteDriverCar_shouldThrowEntityNotFoundException() {
        doReturn(Optional.of(DataUtil.defaultDriverWithCar()))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doReturn(Optional.empty())
                .when(carRepository)
                .findByExternalId(DataUtil.CAR_EXTERNAL_ID);

        assertThrows(EntityNotFoundException.class,
                () -> carService.deleteDriverCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.CAR_EXTERNAL_ID));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(carRepository).findByExternalId(eq(DataUtil.CAR_EXTERNAL_ID));
    }

    @Test
    void deleteDriverCar_shouldThrowCarNotBelongDriverException() {
        Car car = DataUtil.defaultCar();
        Driver driver = DataUtil.defaultDriverWithCar();

        doReturn(Optional.of(driver))
                .when(driverRepository)
                .findByExternalId(DataUtil.DRIVER_EXTERNAL_ID);
        doReturn(Optional.of(car))
                .when(carRepository)
                .findByExternalId(DataUtil.CAR_EXTERNAL_ID);

        driver.getCar().setExternalId(UUID.randomUUID());

        assertThrows(CarNotBelongDriverException.class,
                () -> carService.deleteDriverCar(DataUtil.DRIVER_EXTERNAL_ID, DataUtil.CAR_EXTERNAL_ID));

        verify(driverRepository).findByExternalId(eq(DataUtil.DRIVER_EXTERNAL_ID));
        verify(carRepository).findByExternalId(eq(DataUtil.CAR_EXTERNAL_ID));
    }
}
