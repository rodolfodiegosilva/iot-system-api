package com.iot.system.controller;

import com.iot.system.dto.DeviceDTO;
import com.iot.system.model.Device;
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
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{deviceCode}")
    @Operation(summary = "Get a device by Device Code", description = "Retrieve a device by its Divice Code")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable String deviceCode) {
        DeviceDTO device = deviceService.getDeviceByDeviceCode(deviceCode);
        if (device != null) {
            return ResponseEntity.ok(device);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping
    @Operation(summary = "Add a new device", description = "Add a new device to the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceDTO> addDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.saveDevice(device));
    }

    @PutMapping("/{deviceCode}")
    @Operation(summary = "Update a device", description = "Update an existing device")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable String deviceCode, @RequestBody Device device) throws IllegalAccessException {
        DeviceDTO updatedDevice = deviceService.updateDevice(deviceCode, device);
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
}
