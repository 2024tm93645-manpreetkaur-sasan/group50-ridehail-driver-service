package com.rides.app.driver.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = new ObjectMapper();
    }

    public void send(String topic, Object payload) {
        try {
            String value = mapper.writeValueAsString(payload);

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, value);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send event to topic [{}]: {}", topic, ex.getMessage());
                } else {
                    RecordMetadata meta = result.getRecordMetadata();
                    log.info("Sent event to topic [{}], partition {}, offset {}",
                            topic, meta.partition(), meta.offset());
                }
            });

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payload for topic [{}]: {}", topic, e.getMessage());
        }
    }
}
