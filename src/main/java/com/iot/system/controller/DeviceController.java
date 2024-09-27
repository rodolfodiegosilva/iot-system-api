package com.iot.system.controller;

import com.iot.system.config.JwtAuthenticationFilter;
import com.iot.system.dto.CommandRequest;
import com.iot.system.dto.DeviceRequest;
import com.iot.system.dto.DeviceResponse;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.exception.GlobalExceptionHandler;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.model.Device;
import com.iot.system.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved devices"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Unauthorized\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public List<Device> getAllDevices() {
                return deviceService.getAllDevices();
        }

        @GetMapping("/pageable")
        @Operation(summary = "Get all devices pagination", description = "Retrieve a list of all devices with pagination and filtering")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved devices"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Unauthorized\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<DeviceResponse> getAllDevices(
                        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                        @RequestParam(value = "sortBy", defaultValue = "deviceCode", required = false) String sortBy,
                        @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
                        @RequestParam(value = "deviceStatus", required = false) String deviceStatus,
                        @RequestParam(value = "industryType", required = false) String industryType,
                        @RequestParam(value = "deviceName", required = false) String deviceName,
                        @RequestParam(value = "userName", required = false) String userName,
                        @RequestParam(value = "description", required = false) String description,
                        @RequestParam(value = "deviceCode", required = false) String deviceCode) {
                return ResponseEntity.ok(deviceService.getAllDevices(pageNo, pageSize, sortBy, sortDir, deviceStatus,
                                industryType, deviceName, userName, description, deviceCode));
        }

        @GetMapping("/{deviceCode}")
        @Operation(summary = "Get a device by Device Code", description = "Retrieve a device by its Device Code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved device"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this device", content = @Content(schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }"))),
                        @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Device not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<Device> getDeviceByDeviceCode(@PathVariable String deviceCode) {
                Device device = deviceService.getDeviceByDeviceCode(deviceCode);
                if (device != null) {
                        return ResponseEntity.ok(device);
                } else {
                        return ResponseEntity.status(403).build();
                }
        }

        @PostMapping("/command/{deviceCode}")
        @Operation(summary = "Send a command to a device", description = "Send a command to a device by its Device Code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully sent command to device"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to send command to this device", content = @Content(schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }"))),
                        @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Device not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<Device> sendCommand(@PathVariable String deviceCode,
                        @RequestBody CommandRequest commandRequest) {
                Device device = deviceService.sendCommand(deviceCode, commandRequest);
                if (device != null) {
                        return ResponseEntity.ok(device);
                } else {
                        return ResponseEntity.status(403).build();
                }
        }

        @PostMapping
        @Operation(summary = "Add a new device", description = "Add a new device to the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully added device"),
                        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 400, \"message\": \"Invalid input\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }"))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Unauthorized\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<Device> addNewDevice(@RequestBody DeviceRequest deviceRequest) {
                Device savedDevice = deviceService.saveDevice(deviceRequest);
                return ResponseEntity.ok(savedDevice);
        }

        @PutMapping("/{deviceCode}")
        @Operation(summary = "Update a device", description = "Update an existing device")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully updated device"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this device", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }"))),
                        @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Device not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<Device> updateDevice(@PathVariable String deviceCode, @RequestBody DeviceRequest device)
                        throws IllegalAccessException {
                Device updatedDevice = deviceService.updateDevice(deviceCode, device);
                if (updatedDevice != null) {
                        return ResponseEntity.ok(updatedDevice);
                } else {
                        return ResponseEntity.status(403).build();
                }
        }

        @DeleteMapping("/{deviceCode}")
        @Operation(summary = "Delete a device", description = "Delete a device from the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully deleted device", content = @Content(schema = @Schema(implementation = SuccessResponse.class), examples = @ExampleObject(value = "{ \"status\": 200, \"message\": \"Device was successfully deleted\", \"timestamp\": \"2024-07-12T09:50:24.8405953\" }"))),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this device", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }"))),
                        @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Device not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<SuccessResponse> deleteDevice(@PathVariable String deviceCode)
                        throws IllegalAccessException {
                SuccessResponse response = deviceService.deleteDevice(deviceCode);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/{deviceCode}/monitorings")
        @Operation(summary = "Get paginated monitorings for a device", description = "Retrieve a paginated list of monitorings for a specific device by its device code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved monitorings"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these monitorings", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }"))),
                        @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class), examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Device not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")))
        })
        public ResponseEntity<MonitoringResponse> getMonitoringsByDeviceCode(
                        @PathVariable String deviceCode,
                        @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
                        @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
                        @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
                        @RequestParam(value = "monitoringStatus", required = false) String monitoringStatus,
                        @RequestParam(value = "monitoringCode", required = false) String monitoringCode,
                        @RequestParam(value = "userName", required = false) String userName,
                        @RequestParam(value = "deviceName", required = false) String deviceName,
                        @RequestParam(value = "createdAt", required = false) String createdAt,
                        @RequestParam(value = "updatedAt", required = false) String updatedAt) {
                return ResponseEntity.ok(deviceService.getMonitoringsByDeviceCode(deviceCode, pageNo, pageSize, sortBy,
                                sortDir, monitoringStatus, monitoringCode, userName, deviceName, createdAt, updatedAt));
        }
}
