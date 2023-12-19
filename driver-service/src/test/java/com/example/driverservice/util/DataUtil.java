package com.example.driverservice.util;

import com.example.driverservice.amqp.message.CarInfoMessage;
import com.example.driverservice.amqp.message.DriverInfoMessage;
import com.example.driverservice.dto.request.CarRequest;
import com.example.driverservice.dto.request.DriverRequest;
import com.example.driverservice.dto.request.UpdateCarRequest;
import com.example.driverservice.dto.request.UpdateDriverRequest;
import com.example.driverservice.dto.response.CarResponse;
import com.example.driverservice.dto.response.CreateDriverResponse;
import com.example.driverservice.dto.response.DriverResponse;
import com.example.driverservice.model.entity.Car;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.enums.DriverStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class DataUtil {
    public static final Long DRIVER_ID = 1L;
    public static final UUID DRIVER_EXTERNAL_ID = UUID.randomUUID();
    public static final String DRIVER_FIRST_NAME = "Tegeran";
    public static final String DRIVER_LAST_NAME = "Verusitek";
    public static final String DRIVER_PHONE = "+375257229942";
    public static final Double DRIVER_RATE = 5.0;
    public static final DriverStatus DRIVER_STATUS_AVAILABLE = DriverStatus.AVAILABLE;
    public static final DriverStatus DRIVER_STATUS_CREATED = DriverStatus.CREATED;
    public static final DriverStatus DRIVER_STATUS_NO_CAR = DriverStatus.NO_CAR;
    public static final LocalDateTime DRIVER_CREATED_AT = LocalDateTime.now();

    public static final String DRIVER_NEW_FIRST_NAME = "Cebastian";
    public static final String DRIVER_NEW_LAST_NAME = "Chelliny";
    public static final String DRIVER_NEW_PHONE = "+375259994512";

    public static final String CAR_NEW_LICENSE_PLATE = "7382AV-3";
    public static final String CAR_NEW_MODEL = "Opel Light";
    public static final String CAR_NEW_COLOR = "black";

    public static final Long CAR_ID = 1L;
    public static final UUID CAR_EXTERNAL_ID = UUID.randomUUID();
    public static final String CAR_LICENSE_PLATE = "8382AV-3";
    public static final String CAR_MODEL = "Nissan Zhuk";
    public static final String CAR_COLOR = "red";
    public static final LocalDateTime CAR_CREATED_AT = LocalDateTime.now();

    public static Driver defaultDriverWithNoCarStatus(){
        return Driver.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .driverStatus(DRIVER_STATUS_NO_CAR)
                .createdAt(DRIVER_CREATED_AT)
                .car(null)
                .build();
    }

    public static Driver defaultDriverWithAvailableStatus(){
        return Driver.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .driverStatus(DRIVER_STATUS_AVAILABLE)
                .createdAt(DRIVER_CREATED_AT)
                .car(defaultCar())
                .build();
    }

    public static Driver defaultDriverWithCreatedStatus(){
        return Driver.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .driverStatus(DRIVER_STATUS_CREATED)
                .createdAt(DRIVER_CREATED_AT)
                .car(null)
                .build();
    }

    public static Driver defaultDriverWithCar(){
        return Driver.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .driverStatus(DRIVER_STATUS_AVAILABLE)
                .createdAt(DRIVER_CREATED_AT)
                .car(defaultCar())
                .build();
    }

    public static Car defaultCar(){
        return Car.builder()
                .id(CAR_ID)
                .externalId(CAR_EXTERNAL_ID)
                .licensePlate(CAR_LICENSE_PLATE)
                .model(CAR_MODEL)
                .color(CAR_COLOR)
                .createdAt(CAR_CREATED_AT)
                .driver(initDriver())
                .build();
    }

    public static Driver initDriver(){
        return Driver.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .build();
    }

    public static DriverResponse defaultDriverResponse(){
        return DriverResponse.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .car(null)
                .build();
    }

    public static DriverResponse defaultDriverResponseWithCar(){
        return DriverResponse.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .car(defaultCarResponse())
                .build();
    }

    public static CarResponse defaultCarResponse(){
        return CarResponse.builder()
                .id(CAR_ID)
                .externalId(CAR_EXTERNAL_ID)
                .licensePlate(CAR_LICENSE_PLATE)
                .model(CAR_MODEL)
                .color(CAR_COLOR)
                .build();
    }

    public static DriverRequest defaultDriverRequest(){
        return DriverRequest.builder()
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .build();
    }

    public static CreateDriverResponse defaultCreateDriverResponse(){
        return CreateDriverResponse.builder()
                .id(DRIVER_ID)
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .phone(DRIVER_PHONE)
                .rate(DRIVER_RATE)
                .build();
    }

    public static DriverInfoMessage defaultDriverInfoMessageWithNoCar(){
        return DriverInfoMessage.builder()
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .driverStatus(DRIVER_STATUS_CREATED)
                .carInfoMessage(null)
                .build();
    }

    public static DriverInfoMessage defaultDriverInfoMessageWithCar(){
        return DriverInfoMessage.builder()
                .externalId(DRIVER_EXTERNAL_ID)
                .firstName(DRIVER_FIRST_NAME)
                .lastName(DRIVER_LAST_NAME)
                .driverStatus(DRIVER_STATUS_CREATED)
                .carInfoMessage(defaultCarInfoMessage())
                .build();
    }

    public static CarInfoMessage defaultCarInfoMessage(){
        return CarInfoMessage.builder()
                .carLicensePlate(CAR_LICENSE_PLATE)
                .carModel(CAR_MODEL)
                .carColor(CAR_COLOR)
                .build();
    }

    public static CarRequest defaultCarRequest(){
        return CarRequest.builder()
                .licensePlate(CAR_LICENSE_PLATE)
                .model(CAR_MODEL)
                .color(CAR_COLOR)
                .build();
    }

    public static UpdateCarRequest defaultUpdateCarRequest(){
        return UpdateCarRequest.builder()
                .licensePlate(CAR_NEW_LICENSE_PLATE)
                .model(CAR_NEW_MODEL)
                .color(CAR_NEW_COLOR)
                .build();
    }

    public static UpdateDriverRequest defaultUpdateDriverRequest(){
        return UpdateDriverRequest.builder()
                .firstName(DRIVER_NEW_FIRST_NAME)
                .lastName(DRIVER_NEW_LAST_NAME)
                .phone(DRIVER_NEW_PHONE)
                .build();
    }
}
