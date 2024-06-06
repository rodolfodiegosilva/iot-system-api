package com.iot.system.controller;

import com.iot.system.model.Device;
import com.iot.system.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
@Tag(name = "Device", description = "API for managing IoT devices")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieve a list of all devices")
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a device by ID", description = "Retrieve a device by its ID")
    public ResponseEntity<Device> getDeviceById(
            @Parameter(description = "ID of the device to be retrieved", required = true)
            @PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        if (device == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Add a new device", description = "Add a new device to the system")
    public Device createDevice(@RequestBody Device device) {
        return deviceService.saveDevice(device);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a device", description = "Update an existing device")
    public ResponseEntity<Device> updateDevice(
            @Parameter(description = "ID of the device to be updated", required = true)
            @PathVariable Long id,
            @RequestBody Device deviceDetails) {
        Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
        if (updatedDevice == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedDevice, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a device", description = "Delete a device from the system")
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "ID of the device to be deleted", required = true)
            @PathVariable Long id) {
        deviceService.deleteDevice(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
