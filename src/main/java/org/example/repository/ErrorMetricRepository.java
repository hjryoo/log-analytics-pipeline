package org.example.repository;

import org.example.entity.ErrorMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorMetricRepository extends JpaRepository<ErrorMetricEntity, Long> {
}