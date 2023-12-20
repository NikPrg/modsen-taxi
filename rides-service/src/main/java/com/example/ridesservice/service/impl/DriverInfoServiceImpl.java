package com.example.ridesservice.service.impl;

import com.example.ridesservice.amqp.message.DriverInfoMessage;
import com.example.ridesservice.mapper.DriverInfoMapper;
import com.example.ridesservice.repository.DriverInfoRepository;
import com.example.ridesservice.service.DriverInfoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.ridesservice.util.ExceptionMessagesConstants.*;

@Service
@RequiredArgsConstructor
public class DriverInfoServiceImpl implements DriverInfoService {

    private final DriverInfoRepository driverInfoRepo;
    private final DriverInfoMapper driverInfoMapper;

    @Transactional
    @Override
    public void saveNewDriverData(GenericMessage<DriverInfoMessage> message) {
        var driver = driverInfoMapper.toDriver(message.getPayload());
        driverInfoRepo.save(driver);
    }

    @Transactional
    @Override
    public void updateDriverData(GenericMessage<DriverInfoMessage> message) {
        UUID driverExternalId = message.getPayload().externalId();

        var driver = driverInfoRepo.findByExternalId(driverExternalId)
                .orElseThrow(() -> new EntityNotFoundException(DRIVER_NOT_FOUND_EXCEPTION_MESSAGE.formatted(driverExternalId)));

        driverInfoMapper.updateDriver(message.getPayload(), driver);
    }
}
