package com.example.driverservice.controller;

import com.example.driverservice.dto.response.error.ErrorResponse;
import com.example.driverservice.exception.CarNotBelongDriverException;
import com.example.driverservice.exception.DriverAlreadyHasCarException;
import com.example.driverservice.exception.DriverCarNotFoundException;
import jakarta.persistence.EntityNotFoundException;
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

@Slf4j
@RestControllerAdvice
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
                .statusCode(HttpStatus.NOT_FOUND.value())
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
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DriverCarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handeDriverCarNotFoundException(DriverCarNotFoundException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled driver car not found exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DriverAlreadyHasCarException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeDriverAlreadyHasCarException(DriverAlreadyHasCarException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled driver already has car exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(CarNotBelongDriverException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeCarNotBelongDriverException(CarNotBelongDriverException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled car not belong driver exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}