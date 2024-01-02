package com.example.ridesservice.component.config;

import io.cucumber.java.ParameterType;
import io.cucumber.spring.CucumberContextConfiguration;

import java.util.UUID;

@CucumberContextConfiguration
public class CucumberConfig {

    @ParameterType(".*")
    public UUID uuid(String value) {
        return UUID.fromString(value);
    }
}