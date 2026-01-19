package org.example.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.ErrorMetricEntity;
import org.example.model.AggregatedLogMetric;
import org.example.repository.ErrorMetricRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogMetricSink {

    private final ErrorMetricRepository repository;

    @Transactional
    @KafkaListener(topics = "aggregated-metrics", groupId = "db-sink-group")
    public void consume(AggregatedLogMetric metric) {
        log.info("📥 Received Metric for DB: {}", metric);

        try {
            // String(Instant) -> LocalDateTime 변환
            LocalDateTime start = LocalDateTime.ofInstant(Instant.parse(metric.windowStart()), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(Instant.parse(metric.windowEnd()), ZoneId.systemDefault());

            ErrorMetricEntity entity = ErrorMetricEntity.builder()
                    .serviceName(metric.serviceName())
                    .logLevel(metric.logLevel())
                    .errorCount(metric.errorCount())
                    .windowStart(start)
                    .windowEnd(end)
                    .build();

            repository.save(entity);
            log.info("✅ Saved to MySQL: ID={}", entity.getId());

        } catch (Exception e) {
            log.error("❌ Failed to save metric to DB", e);
        }
    }
}