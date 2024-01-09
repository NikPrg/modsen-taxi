package com.example.driverservice.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class TestcontainersBase {
    public static final String POSTGRES_IMAGE_NAME = "postgres:15.1-alpine";
    public static final String KAFKA_IMAGE_NAME = "confluentinc/cp-kafka:7.2.1";
    public static final String SPRING_KAFKA_BOOTSTRAP_SERVERS = "spring.kafka.bootstrap-servers";
    public static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    public static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    public static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String POSTGRESQL_DRIVER = "postgresql.driver";

    protected static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME);
    protected static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse(KAFKA_IMAGE_NAME));

    static {
        postgresContainer.start();
        kafkaContainer.start();
    }

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {
        registry.add(SPRING_KAFKA_BOOTSTRAP_SERVERS, kafkaContainer::getBootstrapServers);
        registry.add(SPRING_DATASOURCE_URL, postgresContainer::getJdbcUrl);
        registry.add(SPRING_DATASOURCE_PASSWORD, postgresContainer::getPassword);
        registry.add(SPRING_DATASOURCE_USERNAME, postgresContainer::getUsername);
        registry.add(POSTGRESQL_DRIVER, postgresContainer::getDriverClassName);
    }
}
