package org.example.model;

import lombok.Builder;

@Builder
public class LogMessage {
    private String timestamp;   // 발생 시간 (ISO-8601)
    private String logLevel;    // INFO, WARN, ERROR
    private String serviceName; // payment-service, order-service 등
    private String message;     // 로그 내용
    private String userId;      // 사용자 ID (분석용)
    private long responseTime;  // 응답 속도 (ms)
}