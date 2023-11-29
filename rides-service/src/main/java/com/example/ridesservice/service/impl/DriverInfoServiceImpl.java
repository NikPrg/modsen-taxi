package com.example.ridesservice.service.impl;

import com.example.ridesservice.amqp.message.DriverInfoMessage;
import com.example.ridesservice.mapper.DriverInfoMapper;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.service.DriverInfoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.UUID;

import static com.example.ridesservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class DriverInfoServiceImpl implements DriverInfoService {
    private final DriverInfoRepository driverInfoRepo;
    private final DriverInfoMapper driverInfoMapper;
    private final EntityManager entityManager;

    @Override
    public void consumeNewDriverData(DriverInfoMessage driverInfoMessage) {
        var driver = driverInfoMapper.toDriver(driverInfoMessage);
        driverInfoRepo.save(driver);
    }
}
