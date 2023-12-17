package com.example.passengerservice.mapper;

import com.example.passengerservice.dto.request.PassengerRegistrationRequest;
import com.example.passengerservice.dto.request.PassengerRequest;
import com.example.passengerservice.dto.response.CreatePassengerResponse;
import com.example.passengerservice.dto.response.PassengerResponse;
import com.example.passengerservice.dto.response.PaymentMethodResponse;
import com.example.passengerservice.model.Passenger;
import com.example.passengerservice.util.DataUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class PassengerMapperTest {

    private static PassengerMapper passengerMapper;

    @BeforeAll
    static void setUp() {
        passengerMapper = Mappers.getMapper(PassengerMapper.class);
    }

    @Test
    void mapper_isNotNull() {
        assertThat(passengerMapper).isNotNull();
    }

    @Test
    void toPassenger_shouldReturnMappedPassenger() {
        PassengerRegistrationRequest passengerRequest = DataUtil.defaultPassengerRegistrationRequest();
        Passenger expected = DataUtil.defaultPassenger();

        Passenger actual = passengerMapper.toPassenger(passengerRequest);
        actual.setExternalId(DataUtil.PASSENGER_EXTERNAL_ID);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toDto_shouldReturnExpectedResponse() {
        Passenger passenger = DataUtil.defaultPassenger();
        PassengerResponse expected = DataUtil.defaultPassengerResponse();

        PassengerResponse actual = passengerMapper.toDto(passenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updatePassenger_shouldReturnUpdatedPassenger() {
        PassengerRequest request = DataUtil.defaultPassengerRequest();
        Passenger passenger = DataUtil.defaultPassenger();
        Passenger expected = DataUtil.defaultUpdatedPassenger();

        Passenger actual = passengerMapper.updatePassenger(request, passenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toCreateDto_shouldReturnExpectedResponse() {
        Passenger passenger = DataUtil.defaultPassenger();
        CreatePassengerResponse expected = DataUtil.defaultCreatePassengerResponse();

        CreatePassengerResponse actual = passengerMapper.toCreateDto(passenger);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toPaymentInfoDto_shouldReturnExpectedResponse() {
        Passenger passenger = DataUtil.defaultPassenger();
        PaymentMethodResponse expected = DataUtil.defaultPaymentMethodResponseCash();

        PaymentMethodResponse actual = passengerMapper.toPaymentMethodDto(passenger);

        assertThat(actual).isEqualTo(expected);
    }

}
