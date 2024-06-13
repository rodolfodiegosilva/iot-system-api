package com.iot.system.dto;
import com.iot.system.model.DeviceStatus;
import lombok.Data;

@Data
public class MonitoringRequest {
    private String deviceCode;
    private DeviceStatus status;
}
