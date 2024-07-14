package com.iot.system.repository;

import com.iot.system.model.Monitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringRepository extends JpaRepository<Monitoring, Long>, JpaSpecificationExecutor<Monitoring> {
    Optional<Monitoring> findByMonitoringCode(String monitoringCode);

    void deleteByMonitoringCode(String monitoringCode);

    void deleteByDeviceId(Long deviceId);

    List<Monitoring> findByUserId(Long userId);

    Optional<Monitoring> findTopByOrderByCreatedAtDesc();

    boolean existsByMonitoringCode(String monitoringCode);
}
