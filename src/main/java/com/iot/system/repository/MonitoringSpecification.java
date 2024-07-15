package com.iot.system.repository;

import com.iot.system.model.Monitoring;
import com.iot.system.model.MonitoringStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MonitoringSpecification {

    public static Specification<Monitoring> hasMonitoringStatus(MonitoringStatus monitoringStatus) {
        return (root, query, builder) -> monitoringStatus == null ? builder.conjunction() : builder.equal(root.get("monitoringStatus"), monitoringStatus);
    }

    public static Specification<Monitoring> hasDeviceCode(String deviceCode) {
        return (root, query, builder) -> deviceCode == null ? builder.conjunction() : builder.equal(root.get("device").get("deviceCode"), deviceCode);
    }

    public static Specification<Monitoring> hasMonitoringCode(String monitoringCode) {
        return (root, query, builder) -> monitoringCode == null ? builder.conjunction() : builder.like(root.get("monitoringCode"), "%" + monitoringCode + "%");
    }

    public static Specification<Monitoring> hasUserName(String userName) {
        return (root, query, builder) -> userName == null ? builder.conjunction() : builder.like(root.get("user").get("name"), "%" + userName + "%");
    }

    public static Specification<Monitoring> hasDeviceName(String deviceName) {
        return (root, query, builder) -> deviceName == null ? builder.conjunction() : builder.like(root.get("device").get("deviceName"), "%" + deviceName + "%");
    }

    public static Specification<Monitoring> createdAtAfter(LocalDateTime createdAtStart) {
        return (root, query, builder) -> createdAtStart == null ? builder.conjunction() : builder.greaterThanOrEqualTo(root.get("createdAt"), createdAtStart);
    }

    public static Specification<Monitoring> createdAtBefore(LocalDateTime createdAtEnd) {
        return (root, query, builder) -> createdAtEnd == null ? builder.conjunction() : builder.lessThanOrEqualTo(root.get("createdAt"), createdAtEnd);
    }

    public static Specification<Monitoring> updatedAtAfter(LocalDateTime updatedAtStart) {
        return (root, query, builder) -> updatedAtStart == null ? builder.conjunction() : builder.greaterThanOrEqualTo(root.get("updatedAt"), updatedAtStart);
    }

    public static Specification<Monitoring> updatedAtBefore(LocalDateTime updatedAtEnd) {
        return (root, query, builder) -> updatedAtEnd == null ? builder.conjunction() : builder.lessThanOrEqualTo(root.get("updatedAt"), updatedAtEnd);
    }

    public static Specification<Monitoring> hasUserId(Long userId) {
        return (root, query, builder) -> userId == null ? builder.conjunction() : builder.equal(root.get("user").get("id"), userId);
    }
}
