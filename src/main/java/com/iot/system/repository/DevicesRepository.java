package com.iot.system.repository;

import com.iot.system.model.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevicesRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {
    List<Device> findByUserId(Long userId);

    void deleteByDeviceCode(String deviceCode);

    Optional<Device> findByDeviceCode(String deviceCode);

    Optional<Device> findTopByOrderByCreatedAtDesc();

    boolean existsByDeviceCode(String deviceCode);

    Page<Device> findAll(Pageable pageable);
}