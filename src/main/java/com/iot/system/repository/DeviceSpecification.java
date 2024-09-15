package com.iot.system.repository;

import com.iot.system.model.Device;
import com.iot.system.model.DeviceStatus;
import com.iot.system.user.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


public class DeviceSpecification {

    public static Specification<Device> hasDeviceStatus(DeviceStatus deviceStatus) {
        return (root, query, builder) -> deviceStatus == null ? builder.conjunction() : builder.equal(root.get("deviceStatus"), deviceStatus);
    }

    public static Specification<Device> hasIndustryType(String industryType) {
        return (root, query, builder) -> industryType == null ? builder.conjunction() : builder.equal(root.get("industryType"), industryType);
    }

    public static Specification<Device> hasUserName(String userName) {
        return (root, query, builder) -> {
            if (userName == null || userName.isEmpty()) {
                return builder.conjunction();
            }
            Join<Device, User> userJoin = root.join("users");
            return builder.like(userJoin.get("name"), "%" + userName + "%");
        };
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

    public static Specification<Device> hasCreatedBy(String userName) {
        return (root, query, builder) -> userName == null ? builder.conjunction() : builder.like(root.get("users").get("name"), "%" + userName + "%");
    }

    public static Specification<Device> hasUserId(Long userId) {
        return (root, query, builder) -> {
            if (userId == null) {
                return builder.conjunction();
            }
            Join<Device, User> userJoin = root.join("users");
            return builder.equal(userJoin.get("id"), userId);
        };
    }
}
