package com.iot.system.repository;

import com.iot.system.model.Device;
import com.iot.system.model.Monitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringRepository extends JpaRepository<Monitoring, Long>, JpaSpecificationExecutor<Monitoring> {

    Optional<Monitoring> findByMonitoringCode(String monitoringCode);

    @Modifying
    @Transactional
    @Query("DELETE FROM Monitoring m WHERE m.monitoringCode = :monitoringCode")
    void deleteByMonitoringCode(String monitoringCode);

    void deleteByDeviceId(Long deviceId);

    Optional<Monitoring> findTopByOrderByCreatedAtDesc();

    boolean existsByMonitoringCode(String monitoringCode);

    Optional<Monitoring> findByDevice(Device device);

    List<Monitoring> findByUsers_Id(Long userId);
}
