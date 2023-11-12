package com.example.passengerservice.controller;

import com.example.passengerservice.dto.response.error.ErrorResponse;
import com.example.passengerservice.exception.EntityAlreadyExistException;
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
    public ErrorResponse handeValidationException(EntityNotFoundException ex) {
        val errorId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isErrorEnabled()) {
            log.error("Handled entity not found error: msg='{}', errorId='{}", message, errorId);
        }

        return ErrorResponse.builder()
                .id(errorId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handeValidationException(EntityAlreadyExistException ex) {
        val errorId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isErrorEnabled()) {
            log.error("Handled entity already exist error: msg='{}', errorId='{}", message, errorId);
        }

        return ErrorResponse.builder()
                .id(errorId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        val errorId = UUID.randomUUID().toString();
        val message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        if (log.isErrorEnabled()) {
            log.error("Handled method argument not valid error: msg='{}', errorId='{}", message, errorId);
        }

        return ErrorResponse.builder()
                .id(errorId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        val errorId = UUID.randomUUID().toString();
        val message = ex.getMessage();

        if (log.isErrorEnabled()) {
            log.error("Handled illegal argument error: msg='{}', errorId='{}", message, errorId);
        }

        return ErrorResponse.builder()
                .id(errorId)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

    }
}
