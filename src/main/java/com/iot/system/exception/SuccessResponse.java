package com.iot.system.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SuccessResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    public SuccessResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
