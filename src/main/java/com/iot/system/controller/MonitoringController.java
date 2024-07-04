package com.iot.system.controller;

import com.iot.system.dto.MonitoringRequest;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.model.Monitoring;
import com.iot.system.model.MonitoringStatus;
import com.iot.system.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @PostMapping
    @Operation(summary = "Add a new monitoring", description = "Add a new monitoring to the system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Monitoring>> createMonitoring(@RequestBody List<MonitoringRequest> monitoringRequests) {
        return ResponseEntity.ok(monitoringService.createMonitoring(monitoringRequests));
    }

    @GetMapping("/{monitoringCode}")
    @Operation(summary = "Get a monitoring", description = "Get a  monitoring by monitoringCode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Monitoring> getMonitoring(@PathVariable String monitoringCode) {
        return ResponseEntity.ok(monitoringService.getMonitoringByCode(monitoringCode));
    }

    @GetMapping("/pageable")
    @Operation(summary = "Get all Monitoring", description = "Get all Register Monitoring with pagination and filtering")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonitoringResponse> getAllMonitoring(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "status", required = false) MonitoringStatus status,
            @RequestParam(value = "deviceCode", required = false) String deviceCode,
            @RequestParam(value = "monitoringCode", required = false) String monitoringCode,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "deviceName", required = false) String deviceName,
            @RequestParam(value = "createdAt", required = false) String createdAt,
            @RequestParam(value = "updatedAt", required = false) String updatedAt
    ) {
        return ResponseEntity.ok(monitoringService.getAllMonitoring(pageNo, pageSize, sortBy, sortDir, status, deviceCode, monitoringCode, userName, deviceName, createdAt, updatedAt));
    }

    @PutMapping("/{monitoringCode}")
    @Operation(summary = "Update a monitoring", description = "Update a monitoring by monitoringCode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Monitoring> updateMonitoring(@PathVariable String monitoringCode, @RequestBody MonitoringRequest monitoringRequest) {
        return ResponseEntity.ok(monitoringService.updateMonitoring(monitoringCode, monitoringRequest));
    }

    @DeleteMapping("/{monitoringCode}")
    @Operation(summary = "Delete a monitoring", description = "Delete a monitoring by monitoringCode")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMonitoring(@PathVariable String monitoringCode) {
        monitoringService.deleteMonitoring(monitoringCode);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete multiple monitoring", description = "Delete multiple monitoring by monitoringCodes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMultipleMonitoring(@RequestBody List<String> monitoringCodes) {
        monitoringService.deleteMultipleMonitoring(monitoringCodes);
        return ResponseEntity.noContent().build();
    }
}
