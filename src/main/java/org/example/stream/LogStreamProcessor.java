package org.example.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.example.model.AggregatedLogMetric;
import org.example.model.LogMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.time.Instant;

@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@Slf4j
public class LogStreamProcessor {


    private final JsonSerde<LogMessage> logMessageJsonSerde;
    private final JsonSerde<AggregatedLogMetric> aggregatedLogMetricJsonSerde;

    private static final String RAW_LOGS_TOPIC = "raw-logs";
    private static final String AGGREGATED_METRICS_TOPIC = "aggregated-metrics";

    @Bean
    public KStream<String, LogMessage> kStream(StreamsBuilder builder) {
        KStream<String, LogMessage> logStream = builder.stream(
                RAW_LOGS_TOPIC,
                Consumed.with(Serdes.String(), logMessageJsonSerde)
        );

        KStream<String, LogMessage> errorLogStream = logStream
                .filter((key, logMessage) -> "ERROR".equals(logMessage.logLevel())); // record는 getter 대신 필드명으로 접근

        // 1분 단위 Tumbling Window로 서비스별 에러 개수 집계
        KTable<Windowed<String>, Long> errorCountByService = errorLogStream
                .groupBy((key, logMessage) -> logMessage.serviceName(),    // 서비스 이름을 Key로
                        Grouped.with(Serdes.String(), logMessageJsonSerde))
                .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))         // 1분 단위 윈도우
                .count(Materialized.as("ErrorCountStore"));                // 'ErrorCountStore'라는 내부 RocksDB 스토어에 저장

        errorCountByService
                .toStream()
                .map((windowedKey, count) -> {
                    String serviceName = windowedKey.key(); // 윈도우 키에서 서비스 이름 추출
                    Instant windowStart = windowedKey.window().startTime();
                    Instant windowEnd = windowedKey.window().endTime();

                    // 새로운 AggregatedLogMetric 객체 생성
                    AggregatedLogMetric metric = AggregatedLogMetric.builder()
                            .windowStart(windowStart.toString())
                            .windowEnd(windowEnd.toString())
                            .serviceName(serviceName)
                            .logLevel("ERROR")
                            .errorCount(count)
                            .build();

                    log.info("📊 Aggregated Metric: {}", metric);
                    return KeyValue.pair(serviceName, metric); // 서비스 이름을 Key로 다시 설정
                })
                .to(AGGREGATED_METRICS_TOPIC, // aggregated-metrics 토픽으로 전송
                        Produced.with(Serdes.String(), aggregatedLogMetricJsonSerde));

        return logStream; // 스트림을 반환 (특별한 의미는 없음, Bean으로 등록하기 위함)
    }
}
