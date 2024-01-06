package com.example.e2eservice.config;

import io.cucumber.java.ParameterType;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CucumberSpringConfig {
    @ParameterType(".*")
    public UUID uuid(String value) {
        return UUID.fromString(value);
    }
}
