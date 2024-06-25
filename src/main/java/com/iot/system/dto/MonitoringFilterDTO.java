package com.iot.system.dto;

import com.iot.system.model.MonitoringStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitoringFilterDTO {
    private int pageNo = 0;
    private int pageSize = 10;
    private String sortBy = "id";
    private String sortDir = "asc";
    private MonitoringStatus status;
    private String deviceCode;
    private String monitoringCode;
    private String userName;
    private String deviceName;
    private String createdAt;
    private String updatedAt;
}
