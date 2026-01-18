CREATE TABLE IF NOT EXISTS error_metrics (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             service_name VARCHAR(50) NOT NULL,
    log_level VARCHAR(10) NOT NULL,
    error_count BIGINT NOT NULL,
    window_start DATETIME NOT NULL,
    window_end DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY unique_metric (service_name, window_start, window_end)
    );