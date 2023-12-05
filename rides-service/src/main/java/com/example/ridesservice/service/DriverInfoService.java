package com.example.ridesservice.service;

import com.example.ridesservice.amqp.message.DriverInfoMessage;
import org.springframework.messaging.support.GenericMessage;

public interface DriverInfoService {
    void saveNewDriverData(GenericMessage<DriverInfoMessage> message);

    void updateDriverData(GenericMessage<DriverInfoMessage> message);
}
