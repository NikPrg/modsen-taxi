package com.example.cardservice.controller;

import com.example.cardservice.dto.response.error.ErrorResponse;
import com.example.cardservice.exception.EntityAlreadyExistException;
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
    @ExceptionHandler(EntityAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeEntityAlreadyExistException(EntityAlreadyExistException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled entity already exist exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        val exceptionId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isInfoEnabled()) {
            log.info("Handled illegal argument exception: msg='{}', exceptionId='{}", message, exceptionId);
        }

        return ErrorResponse.builder()
                .id(exceptionId)
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
