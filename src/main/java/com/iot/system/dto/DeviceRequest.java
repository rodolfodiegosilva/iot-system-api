package com.iot.system.dto;
import com.iot.system.model.CommandDescription;
import com.iot.system.model.DeviceStatus;
import lombok.Data;

import java.util.List;

@Data
public class DeviceRequest {
    private String deviceCode;
    private String deviceName;
    private String description;
    private String industryType;
    private String manufacturer;
    private String url;
    private DeviceStatus deviceStatus;
    private List<String> usernames;
    private List<CommandDescription> commands;
}
