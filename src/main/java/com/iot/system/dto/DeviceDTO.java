package com.iot.system.dto;

import lombok.Data;

@Data
public class DeviceDTO {
    private Long id;
    private String name;
    private String description;
    private UserDTO user;
}