// src/main/java/com/iot/system/repository/DeviceRepository.java
package com.iot.system.repository;

import com.iot.system.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
