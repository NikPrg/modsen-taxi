package com.example.ridesservice.service;

import com.example.ridesservice.amqp.message.DriverInfoMessage;

public interface DriverInfoService {
    void consumeNewDriverData(DriverInfoMessage driverInfoMessage);
}
