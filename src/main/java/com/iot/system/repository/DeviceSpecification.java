package com.iot.system.repository;

import com.iot.system.model.Device;
import com.iot.system.model.DeviceStatus;
import org.springframework.data.jpa.domain.Specification;

public class DeviceSpecification {

    public static Specification<Device> hasDeviceStatus(DeviceStatus deviceStatus) {
        return (root, query, builder) -> deviceStatus == null ? builder.conjunction() : builder.equal(root.get("deviceStatus"), deviceStatus);
    }

    public static Specification<Device> hasIndustryType(String industryType) {
        return (root, query, builder) -> industryType == null ? builder.conjunction() : builder.equal(root.get("industryType"), industryType);
    }

    public static Specification<Device> hasUserName(String userName) {
        return (root, query, builder) -> userName == null ? builder.conjunction() : builder.like(root.get("user").get("name"), "%" + userName + "%");
    }

    public static Specification<Device> hasDeviceName(String deviceName) {
        return (root, query, builder) -> deviceName == null ? builder.conjunction() : builder.like(root.get("deviceName"), "%" + deviceName + "%");
    }

    public static Specification<Device> hasDescription(String description) {
        return (root, query, builder) -> description == null ? builder.conjunction() : builder.like(root.get("description"), "%" + description + "%");
    }

    public static Specification<Device> hasDeviceCode(String deviceCode) {
        return (root, query, builder) -> deviceCode == null ? builder.conjunction() : builder.like(root.get("deviceCode"), "%" + deviceCode + "%");
    }

    public static Specification<Device> hasUserId(Long userId) {
        return (root, query, builder) -> userId == null ? builder.conjunction() : builder.equal(root.get("user").get("id"), userId);
    }
}
