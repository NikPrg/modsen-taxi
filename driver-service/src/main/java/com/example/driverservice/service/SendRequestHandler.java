package com.example.driverservice.service;

import com.example.driverservice.amqp.message.DriverInfoMessage;

public interface SendRequestHandler {
    void handleRequest(DriverInfoMessage payload);
}
