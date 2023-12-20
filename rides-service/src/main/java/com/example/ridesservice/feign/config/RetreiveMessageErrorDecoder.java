package com.example.ridesservice.feign.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class RetreiveMessageErrorDecoder implements ErrorDecoder {

    private static final Pattern MESSAGE_EXTRACTOR_PATTERN = Pattern.compile("\"message\":\"([^\"]+)\"");

    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            message = IOUtils.toString(bodyIs, StandardCharsets.UTF_8);
            var matcher = MESSAGE_EXTRACTOR_PATTERN.matcher(message);
            if (matcher.find()) {
                message = matcher.group(1);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        switch (response.status()) {
            case 404:
                return new EntityNotFoundException(message);
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }
}
