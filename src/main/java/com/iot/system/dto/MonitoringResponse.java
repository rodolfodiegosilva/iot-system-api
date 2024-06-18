package com.iot.system.dto;


import com.iot.system.model.Monitoring;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MonitoringResponse {
    private List<Monitoring> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}