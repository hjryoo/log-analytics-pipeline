package org.example.model;

import lombok.Builder;

@Builder
public record LogMessage(
        String timestamp,
        String logLevel,
        String serviceName,
        String message,
        String userId,
        long responseTime
) {}