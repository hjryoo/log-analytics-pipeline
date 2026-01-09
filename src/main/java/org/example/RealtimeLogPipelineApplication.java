package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 추가!

@EnableScheduling // 스케줄러 활성화 어노테이션 필수
@SpringBootApplication
public class RealtimeLogPipelineApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealtimeLogPipelineApplication.class, args);
    }
}