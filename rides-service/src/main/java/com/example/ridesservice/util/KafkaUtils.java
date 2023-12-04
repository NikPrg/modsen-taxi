package com.example.ridesservice.util;

public class KafkaUtils {
    public static String buildTypeMappings(Class<?>... classes) {
        StringBuilder typeMappingsBuilder = new StringBuilder();
        for (Class<?> clazz : classes) {
            if (!typeMappingsBuilder.isEmpty()) {
                typeMappingsBuilder.append(",");
            }
            typeMappingsBuilder.append(clazz.getSimpleName()).append(":").append(clazz.getName());
        }
        return typeMappingsBuilder.toString();
    }
}
