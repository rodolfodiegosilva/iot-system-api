
package com.iot.system.service;

import com.iot.system.dto.MonitoringRequest;
import com.iot.system.model.Device;
import com.iot.system.model.Monitoring;
import com.iot.system.repository.DeviceRepository;
import com.iot.system.repository.MonitoringRepository;
import com.iot.system.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final MonitoringRepository monitoringRepository;
    private final DeviceRepository deviceRepository;
    private final UserService userService;

    public List<Monitoring> createMonitoring(final List<MonitoringRequest> monitoringRequests) {

        List<Monitoring> monitoringToAdd = new ArrayList<>();
        for (MonitoringRequest request : monitoringRequests) {
            final Device device = deviceRepository.findByDeviceCode(request.getDeviceCode())
                    .orElseThrow(() -> new IllegalArgumentException("Device not found"));

            Monitoring monitoring = new Monitoring();
            monitoring.setMonitoringCode(generateMonitoringCode());
            monitoring.setUser(userService.getCurrentUser());
            monitoring.setDevice(device);
            monitoring.setStatus(request.getStatus());
            monitoring.setCreatedAt(LocalDateTime.now());
            monitoring.setUpdatedAt(LocalDateTime.now());
            monitoringToAdd.add(monitoring);
        }

        return monitoringRepository.saveAll(monitoringToAdd);
    }

    public Monitoring getMonitoringByCode(final String monitoringCode) {
        Monitoring monitoring = monitoringRepository.findByMonitoringCode(monitoringCode)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring not found"));
        User currentUser = userService.getCurrentUser();
        if (!monitoring.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("User not authorized to view this monitoring");
        }
        return monitoring;
    }

    public List<Monitoring> getAllMonitoring() {
        final User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().name().equals("ADMIN")) {
            return monitoringRepository.findAll();
        }
        return monitoringRepository.findByUserId(currentUser.getId());
    }

    public Monitoring updateMonitoring(final String monitoringCode, final MonitoringRequest monitoringRequest) {
        Monitoring monitoring = monitoringRepository.findByMonitoringCode(monitoringCode)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring not found"));
        final User currentUser = userService.getCurrentUser();
        if (!monitoring.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("User not authorized to update this monitoring");
        }
        if (!monitoring.getDevice().getDeviceCode().equals(monitoringRequest.getDeviceCode())) {
            final Device device = deviceRepository.findByDeviceCode(monitoringRequest.getDeviceCode()).orElse(null);
            monitoring.setDevice(device);
        }
        monitoring.setStatus(monitoringRequest.getStatus());
        return monitoringRepository.save(monitoring);
    }

    public void deleteMonitoring(final String monitoringCode) {
        Monitoring monitoring = monitoringRepository.findByMonitoringCode(monitoringCode)
                .orElseThrow(() -> new IllegalArgumentException("Monitoring not found"));
        final User currentUser = userService.getCurrentUser();
        if (!monitoring.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("User not authorized to delete this monitoring");
        }
        monitoringRepository.deleteByMonitoringCode(monitoringCode);
    }

    public void deleteMultipleMonitoring(final List<String> monitoringCodes) {
        for (String monitoringCode : monitoringCodes) {
            deleteMonitoring(monitoringCode);
        }
    }

    private String generateMonitoringCode() {
        String lastMonitoringCode = monitoringRepository.findTopByOrderByCreatedAtDesc()
                .map(Monitoring::getMonitoringCode)
                .orElse("MT0000");

        int lastNumber = Integer.parseInt(lastMonitoringCode.substring(2));
        String newMonitoringCode;

        do {
            lastNumber++;
            newMonitoringCode = "MT" + String.format("%04d", lastNumber);
        } while (monitoringRepository.existsByMonitoringCode(newMonitoringCode));

        return newMonitoringCode;
    }
}
