package com.iot.system.dto;

import com.iot.system.model.MonitoringStatus;
import lombok.Data;

@Data
public class MonitoringRequest {
    private String deviceCode;
    private MonitoringStatus monitoringStatus;
    private String description;
}
