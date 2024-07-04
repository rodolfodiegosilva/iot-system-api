package com.iot.system.controller;

import com.iot.system.dto.DeviceResponse;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.model.Device;
import com.iot.system.model.MonitoringStatus;
import com.iot.system.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
@Tag(name = "Device", description = "API for managing IoT devices")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieve a list of all devices")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/pageable")
    @Operation(summary = "Get all devices pagination", description = "Retrieve a list of all devices with pagination and filtering")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DeviceResponse> getAllDevices(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "industryType", required = false) String industryType,
            @RequestParam(value = "deviceName", required = false) String deviceName,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "deviceCode", required = false) String deviceCode
    ) {
        return ResponseEntity.ok(deviceService.getAllDevices(pageNo, pageSize, sortBy, sortDir, status, industryType, deviceName, userName, description, deviceCode));
    }

    @GetMapping("/{deviceCode}")
    @Operation(summary = "Get a device by Device Code", description = "Retrieve a device by its Device Code")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Device> getDeviceById(@PathVariable String deviceCode) {
        Device device = deviceService.getDeviceByDeviceCode(deviceCode);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping
    @Operation(summary = "Add a new device", description = "Add a new device to the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Device> addDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.saveDevice(device));
    }

    @PutMapping("/{deviceCode}")
    @Operation(summary = "Update a device", description = "Update an existing device")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Device> updateDevice(@PathVariable String deviceCode, @RequestBody Device device)
            throws IllegalAccessException {
        Device updatedDevice = deviceService.updateDevice(deviceCode, device);
        if (updatedDevice != null) {
            return ResponseEntity.ok(updatedDevice);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a device", description = "Delete a device from the system")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) throws IllegalAccessException {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deviceCode}/monitorings")
    @Operation(summary = "Get paginated monitorings for a device", description = "Retrieve a paginated list of monitorings for a specific device by its device code")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MonitoringResponse> getMonitoringsByDeviceCode(
            @PathVariable String deviceCode,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "status", required = false) MonitoringStatus status,
            @RequestParam(value = "monitoringCode", required = false) String monitoringCode,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "deviceName", required = false) String deviceName,
            @RequestParam(value = "createdAt", required = false) String createdAt,
            @RequestParam(value = "updatedAt", required = false) String updatedAt
    ) {
        return ResponseEntity.ok(deviceService.getMonitoringsByDeviceCode(deviceCode, pageNo, pageSize, sortBy, sortDir, status, monitoringCode, userName, deviceName, createdAt, updatedAt));
    }
}
