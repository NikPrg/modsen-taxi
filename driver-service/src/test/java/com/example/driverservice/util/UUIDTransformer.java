package com.example.driverservice.util;

import io.cucumber.cucumberexpressions.Transformer;

import java.util.UUID;

public class UUIDTransformer implements Transformer<UUID> {
    @Override
    public UUID transform(String value) {
        return UUID.fromString(value);
    }
}
