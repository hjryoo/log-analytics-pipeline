package org.example.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.LogMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_NAME = "raw-logs";

    public void sendMessage(LogMessage logMessage) {
        kafkaTemplate.send(TOPIC_NAME, logMessage);
        log.info("Log Sent: {}", logMessage);
    }
}