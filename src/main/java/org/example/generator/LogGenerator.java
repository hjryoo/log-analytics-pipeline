package org.example.generator;

import lombok.RequiredArgsConstructor;
import org.example.model.LogMessage;
import org.example.producer.LogProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.random.RandomGenerator; // Java 17+ 새로운 랜덤 패키지

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.generator.enabled", havingValue = "true")
public class LogGenerator {

    private final LogProducer logProducer;
    private final RandomGenerator random = RandomGenerator.getDefault();

    private final List<String> serviceNames = List.of("order-service", "payment-service", "user-service");
    private final List<String> errorMessages = List.of("NullPointerException", "TimeoutException", "DatabaseConnectionError");
    private final List<String> infoMessages = List.of("User login", "Order created", "Payment processed");

    @Scheduled(fixedRateString = "${app.generator.rate-ms}")
    public void generateLog() {
        String level = getRandomLogLevel();
        String service = serviceNames.get(random.nextInt(serviceNames.size()));

        String message = "ERROR".equals(level) ?
                errorMessages.get(random.nextInt(errorMessages.size())) :
                infoMessages.get(random.nextInt(infoMessages.size()));

        long responseTime = "ERROR".equals(level) ? random.nextInt(500, 1500) : random.nextInt(0, 200);

        LogMessage logMessage = LogMessage.builder()
                .timestamp(Instant.now().toString())
                .logLevel(level)
                .serviceName(service)
                .message(message)
                .userId("user-" + random.nextInt(100))
                .responseTime(responseTime)
                .build();

        logProducer.sendMessage(logMessage);
    }

    private String getRandomLogLevel() {
        int chance = random.nextInt(100);
        if (chance < 20) return "ERROR";
        if (chance < 50) return "WARN";
        return "INFO";
    }
}