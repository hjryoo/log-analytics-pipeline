package org.example.model;

import lombok.Builder;

@Builder
public record AggregatedLogMetric(
        String windowStart,
        String windowEnd,
        String serviceName,
        String logLevel,
        Long errorCount
) {}