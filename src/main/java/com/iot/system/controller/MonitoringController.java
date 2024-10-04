package com.iot.system.controller;

import com.iot.system.config.JwtAuthenticationFilter;
import com.iot.system.dto.MonitoringRequest;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.exception.GlobalExceptionHandler;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.model.Monitoring;
import com.iot.system.model.MonitoringStatus;
import com.iot.system.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping
    @Operation(summary = "Get all monitorings", description = "Retrieve a list of all monitorings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved monitorings"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Unauthorized\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public List<Monitoring> getAllMonitorings() {
        return monitoringService.getAllMonitorings();
    }

    @PostMapping
    @Operation(summary = "Add a new monitoring", description = "Add a new monitoring to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added monitoring"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 400, \"message\": \"Invalid input\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Unauthorized\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<List<Monitoring>> createMonitoring(@RequestBody List<MonitoringRequest> monitoringRequests) {
        return ResponseEntity.ok(monitoringService.createMonitoring(monitoringRequests));
    }

    @GetMapping("/{monitoringCode}")
    @Operation(summary = "Get a monitoring", description = "Get a monitoring by monitoringCode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved monitoring"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this monitoring", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            )),
            @ApiResponse(responseCode = "404", description = "Monitoring not found", content = @Content(
                    schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Monitoring not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<Monitoring> getMonitoring(@PathVariable String monitoringCode) {
        return ResponseEntity.ok(monitoringService.getMonitoringByCode(monitoringCode));
    }

    @GetMapping("/pageable")
    @Operation(summary = "Get all Monitoring", description = "Get all Register Monitoring with pagination and filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved monitorings"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these monitorings", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            )),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content(
                    schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Device not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<MonitoringResponse> getAllMonitoring(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "monitoringCode", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "monitoringStatus", required = false) MonitoringStatus monitoringStatus,
            @RequestParam(value = "deviceCode", required = false) String deviceCode,
            @RequestParam(value = "monitoringCode", required = false) String monitoringCode,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "deviceName", required = false) String deviceName,
            @RequestParam(value = "createdAt", required = false) String createdAt,
            @RequestParam(value = "updatedAt", required = false) String updatedAt
    ) {
        return ResponseEntity.ok(monitoringService.getAllMonitoring(pageNo, pageSize, sortBy, sortDir, monitoringStatus, deviceCode, monitoringCode, userName, deviceName, createdAt, updatedAt));
    }

    @PutMapping("/{monitoringCode}")
    @Operation(summary = "Update a monitoring", description = "Update a monitoring by monitoringCode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated monitoring"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this monitoring", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            )),
            @ApiResponse(responseCode = "404", description = "Monitoring not found", content = @Content(
                    schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Monitoring not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<Monitoring> updateMonitoring(@PathVariable String monitoringCode, @RequestBody MonitoringRequest monitoringRequest) {
        return ResponseEntity.ok(monitoringService.updateMonitoring(monitoringCode, monitoringRequest));
    }

    @DeleteMapping("/{monitoringCode}")
    @Operation(summary = "Delete a monitoring", description = "Delete a monitoring by monitoringCode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted monitoring"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this monitoring", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            )),
            @ApiResponse(responseCode = "404", description = "Monitoring not found", content = @Content(
                    schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Monitoring not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<SuccessResponse> deleteMonitoring(@PathVariable String monitoringCode) {
        SuccessResponse response = monitoringService.deleteMonitoring(monitoringCode);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Delete multiple monitoring", description = "Delete multiple monitoring by monitoringCodes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted monitorings"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete these monitorings", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 403, \"message\": \"Forbidden\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            )),
            @ApiResponse(responseCode = "404", description = "One or more monitorings not found", content = @Content(
                    schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"Monitoring not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<Void> deleteMultipleMonitoring(@RequestBody List<String> monitoringCodes) {
        monitoringService.deleteMultipleMonitoring(monitoringCodes);
        return ResponseEntity.noContent().build();
    }
}
