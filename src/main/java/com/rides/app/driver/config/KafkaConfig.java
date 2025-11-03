package com.rides.app.driver.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.driver-registered:driver.registered}")
    private String driverRegisteredTopic;

    @Value("${app.kafka.topic.driver-status:driver.status.changed}")
    private String driverStatusTopic;

    @Value("${app.kafka.topic.driver-location:driver.location.updated}")
    private String driverLocationTopic;

    @Bean
    public NewTopic driverRegisteredTopic() {
        return new NewTopic(driverRegisteredTopic, 1, (short)1);
    }

    @Bean
    public NewTopic driverStatusTopic() {
        return new NewTopic(driverStatusTopic, 1, (short)1);
    }

    @Bean
    public NewTopic driverLocationTopic() {
        return new NewTopic(driverLocationTopic, 1, (short)1);
    }
}

