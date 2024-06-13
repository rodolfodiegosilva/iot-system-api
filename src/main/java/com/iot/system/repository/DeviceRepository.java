package com.iot.system.repository;

import com.iot.system.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(Long userId);
    Optional<Device> findByDeviceCode(String deviceCode);
    Optional<Device> findTopByOrderByCreatedAtDesc();
    boolean existsByDeviceCode(String deviceCode);
}
