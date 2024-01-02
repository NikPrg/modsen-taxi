package com.example.driverservice.integration.kafka;

import com.example.driverservice.amqp.message.DriverStatusMessage;
import com.example.driverservice.integration.TestcontainersBase;
import com.example.driverservice.model.entity.Driver;
import com.example.driverservice.model.enums.DriverStatus;
import com.example.driverservice.repository.DriverRepository;
import com.example.driverservice.util.EntitiesUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
public class KafkaConsumerIntegrationTest extends TestcontainersBase {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private DriverRepository driverRepo;

    @Value("${app.kafka.topic.driver-status-events}")
    private String driverStatusEventsTopic;

    @Test
    void updateDriverStatus_shouldSetUnavailableStatus(){
        // arrange
        Driver driver = EntitiesUtil.vladAvailableDriver();
        driverRepo.save(driver);
        DriverStatusMessage message = new DriverStatusMessage(driver.getExternalId(), DriverStatus.UNAVAILABLE);

        // act
        kafkaTemplate.send(driverStatusEventsTopic, message);

        // assert
        await()
                .pollInterval(Duration.ofSeconds(5))
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Driver actual = driverRepo.findByExternalId(driver.getExternalId()).get();
                    assertThat(actual.getDriverStatus()).isEqualTo(DriverStatus.UNAVAILABLE);
                });
    }
}
