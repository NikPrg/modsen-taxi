package com.example.cardservice.service.impl;

import com.example.cardservice.amqp.message.NewPassengerInfoMessage;
import com.example.cardservice.amqp.message.RemovePassengerInfoMessage;
import com.example.cardservice.mapper.PassengerInfoMapper;
import com.example.cardservice.repository.PassengerInfoRepository;
import com.example.cardservice.service.PassengerInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerInfoServiceImpl implements PassengerInfoService {

    private final PassengerInfoRepository passengerInfoRepo;
    private final PassengerInfoMapper passengerInfoMapper;

    @Transactional
    @Override
    public void saveNewPassengerInfo(NewPassengerInfoMessage message) {
        passengerInfoRepo.save(passengerInfoMapper.toPassengerInfo(message));
    }
    @Transactional
    @Override
    public void removePassengerInfo(RemovePassengerInfoMessage message) {
        passengerInfoRepo.deleteByExternalId(message.passengerExternalId());
    }
}
