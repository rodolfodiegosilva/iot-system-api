package com.iot.system.service;

import com.iot.system.dto.DeviceResponse;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.UnauthorizedException;
import com.iot.system.model.Device;
import com.iot.system.model.Monitoring;
import com.iot.system.model.MonitoringStatus;
import com.iot.system.repository.DevicesRepository;
import com.iot.system.repository.DeviceSpecification;
import com.iot.system.repository.MonitoringRepository;
import com.iot.system.repository.MonitoringSpecification;
import com.iot.system.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DevicesRepository devicesRepository;

    @Autowired
    private MonitoringRepository monitoringRepository;

    private final UserService userService;

    public DeviceService(UserService userService) {
        this.userService = userService;
    }

    public List<Device> getAllDevices() {
        final User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().name().equals("ADMIN")) {
            return devicesRepository.findAll();
        }
        return devicesRepository.findByUserId(currentUser.getId());
    }

    public DeviceResponse getAllDevices(int pageNo, int pageSize, String sortBy, String sortDir, String status,
            String industryType, String deviceName, String userName, String description, String deviceCode) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        final User currentUser = userService.getCurrentUser();

        Specification<Device> spec = Specification.where(DeviceSpecification.hasStatus(status))
                .and(DeviceSpecification.hasIndustryType(industryType))
                .and(DeviceSpecification.hasUserName(userName))
                .and(DeviceSpecification.hasDeviceName(deviceName))
                .and(DeviceSpecification.hasDescription(description))
                .and(DeviceSpecification.hasDeviceCode(deviceCode));

        if (!currentUser.getRole().name().equals("ADMIN")) {
            spec = spec.and(DeviceSpecification.hasUserId(currentUser.getId()));
        }

        Page<Device> devices = devicesRepository.findAll(spec, pageable);
        List<Device> content = devices.getContent();

        return DeviceResponse.builder()
                .content(content)
                .pageNo(devices.getNumber())
                .pageSize(devices.getSize())
                .totalElements(devices.getTotalElements())
                .totalPages(devices.getTotalPages())
                .last(devices.isLast())
                .build();
    }

    public Device getDeviceByDeviceCode(String deviceCode) {
        Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to view this device");
        }
        return device;
    }

    public Device saveDevice(Device device) {
        User currentUser = userService.getCurrentUser();
        device.setUser(currentUser);
        device.setDeviceCode(generateDeviceCode());
        return devicesRepository.save(device);
    }

    public Device updateDevice(String deviceCode, Device deviceDetails) {
        Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to update this device");
        }
        device.setDeviceName(deviceDetails.getDeviceName());
        device.setDescription(deviceDetails.getDescription());
        device.setDeviceStatus(deviceDetails.getDeviceStatus());
        return devicesRepository.save(device);
    }

    public void deleteDevice(Long id) {
        Device device = devicesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device");
        }
        devicesRepository.deleteById(id);
    }

    public MonitoringResponse getMonitoringsByDeviceCode(String deviceCode, int pageNo, int pageSize, String sortBy,
            String sortDir,
            MonitoringStatus status, String monitoringCode, String userName, String deviceName,
            String createdAt, String updatedAt) {
        logger.info("Fetching monitorings for deviceCode: {}", deviceCode);

        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());
        final User currentUser = userService.getCurrentUser();

        LocalDateTime[] createdAtRange = parseDateRange(createdAt);
        LocalDateTime[] updatedAtRange = parseDateRange(updatedAt);

        Specification<Monitoring> spec = Specification.where(MonitoringSpecification.hasDeviceCode(deviceCode))
                .and(MonitoringSpecification.hasStatus(status))
                .and(MonitoringSpecification.hasMonitoringCode(monitoringCode))
                .and(MonitoringSpecification.hasUserName(userName))
                .and(MonitoringSpecification.hasDeviceName(deviceName))
                .and(MonitoringSpecification.createdAtAfter(createdAtRange[0]))
                .and(MonitoringSpecification.createdAtBefore(createdAtRange[1]))
                .and(MonitoringSpecification.updatedAtAfter(updatedAtRange[0]))
                .and(MonitoringSpecification.updatedAtBefore(updatedAtRange[1]));

        Page<Monitoring> monitorings;

        if (currentUser.getRole().name().equals("ADMIN")) {
            logger.info("User is ADMIN");
            monitorings = monitoringRepository.findAll(spec, pageable);
        } else {
            logger.info("User is not ADMIN, adding user filter");
            spec = spec.and((root, query, builder) -> builder.equal(root.get("user").get("id"), currentUser.getId()));
            monitorings = monitoringRepository.findAll(spec, pageable);
        }

        logger.info("Found {} monitorings", monitorings.getTotalElements());

        List<Monitoring> content = monitorings.getContent();

        return MonitoringResponse.builder()
                .content(content)
                .pageNo(monitorings.getNumber())
                .pageSize(monitorings.getSize())
                .totalElements(monitorings.getTotalElements())
                .totalPages(monitorings.getTotalPages())
                .last(monitorings.isLast())
                .build();
    }

    private LocalDateTime[] parseDateRange(String dateRange) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (dateRange != null && !dateRange.isEmpty()) {
            try {
                if (dateRange.contains("-")) {
                    String[] dates = dateRange.split("-");
                    start = LocalDate.parse(dates[0].trim(), formatter).atStartOfDay();
                    end = LocalDate.parse(dates[1].trim(), formatter).atTime(23, 59, 59);
                } else {
                    start = LocalDate.parse(dateRange.trim(), formatter).atStartOfDay();
                    end = start.withHour(23).withMinute(59).withSecond(59);
                }
            } catch (DateTimeParseException e) {
                logger.error("Error parsing date range: {}", dateRange, e);
            }
        }
        return new LocalDateTime[] { start, end };
    }

    private String generateDeviceCode() {
        String lastDeviceCode = devicesRepository.findTopByOrderByCreatedAtDesc()
                .map(Device::getDeviceCode)
                .orElse("DVC00000");

        int lastNumber = Integer.parseInt(lastDeviceCode.substring(3));
        String newDeviceCode;

        do {
            lastNumber++;
            newDeviceCode = "DVC" + String.format("%05d", lastNumber);
        } while (devicesRepository.existsByDeviceCode(newDeviceCode));

        return newDeviceCode;
    }
}
