package com.iot.system.repository;

import com.iot.system.model.Monitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonitoringRepository extends JpaRepository<Monitoring, Long> {
    Optional<Monitoring> findByMonitoringCode(String monitoringCode);

    void deleteByMonitoringCode(String monitoringCode);

    Page<Monitoring> findByUserId(Long userId, Pageable pageable);

    Optional<Monitoring> findTopByOrderByCreatedAtDesc();

    boolean existsByMonitoringCode(String monitoringCode);
}
