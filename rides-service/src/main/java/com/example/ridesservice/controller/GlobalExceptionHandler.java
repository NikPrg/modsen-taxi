package com.example.ridesservice.controller;

import com.example.ridesservice.dto.response.error.ErrorResponse;
import com.example.ridesservice.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handeEntityNotFoundException(EntityNotFoundException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled entity not found exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        if (log.isInfoEnabled()) {
            log.info("Handled method argument not valid exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DriverAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeDriverAlreadyInUseException(DriverAlreadyInUseException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled driver already in use exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DriverNotBelongRideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeDriverNotBelongRideException(DriverNotBelongRideException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled driver not belong to ride exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(PassengerRideNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handePassengerRideNotFoundException(PassengerRideNotFoundException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled passenger ride not found exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(RideAlreadyFinishedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeRideAlreadyFinishedExceptionMessage(RideAlreadyFinishedException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled ride already finished exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(RideAlreadyStartedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeRideAlreadyStartedExceptionMessage(RideAlreadyStartedException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled ride already started exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(RideNotAcceptedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeRideNotAcceptedException(RideNotAcceptedException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled ride was not accepted exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(RideNotStartedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeRideNotStartedExceptionMessage(RideNotStartedException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled ride was not started exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
