package com.iot.system.service;

import com.iot.system.dto.CommandRequest;
import com.iot.system.dto.DeviceResponse;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.exception.UnauthorizedException;
import com.iot.system.model.*;
import com.iot.system.repository.DeviceSpecification;
import com.iot.system.repository.DevicesRepository;
import com.iot.system.repository.MonitoringRepository;
import com.iot.system.repository.MonitoringSpecification;
import com.iot.system.user.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DevicesRepository devicesRepository;

    @Autowired
    private MonitoringRepository monitoringRepository;

    @Value("${url.environment}")
    private String urlEnvironment;

    private final UserService userService;

    public DeviceService(@NonNull final UserService userService) {
        this.userService = userService;
    }

    public Device saveDevice(@NonNull final Device device) {
        final User currentUser = userService.getCurrentUser();
        device.setUser(currentUser);
        device.setDeviceCode(generateDeviceCode());
        device.setUrl(urlEnvironment + "/devices/command/" + device.getDeviceCode());
        return devicesRepository.save(device);
    }

    public Device getDeviceByDeviceCode(@NonNull final String deviceCode) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to view this device");
        }
        return device;
    }

    public Device updateDevice(@NonNull final String deviceCode, @NonNull final Device deviceRequest) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to update this device");
        }

        device.setDeviceName(deviceRequest.getDeviceName());
        device.setDescription(deviceRequest.getDescription());
        device.setIndustryType(deviceRequest.getIndustryType());
        device.setManufacturer(deviceRequest.getManufacturer());
        device.setUrl(urlEnvironment + "/devices/command/" + device.getDeviceCode());
        device.setDeviceStatus(deviceRequest.getDeviceStatus());
        device.getCommands().clear();
        final Device updatedDevice = devicesRepository.save(device);

        deviceRequest.getCommands().forEach(commandDescription -> {
            final CommandDescription newCommandDescription = new CommandDescription();
            newCommandDescription.setOperation(commandDescription.getOperation());
            newCommandDescription.setDescription(commandDescription.getDescription());
            newCommandDescription.setResult(commandDescription.getResult());
            newCommandDescription.setFormat(commandDescription.getFormat());
            newCommandDescription.setDevice(updatedDevice);

            final Command newCommand = new Command();
            newCommand.setCommand(commandDescription.getCommand().getCommand());

            final List<Parameter> newParameters = commandDescription.getCommand().getParameters().stream().map(parameter -> {
                final Parameter newParameter = new Parameter();
                newParameter.setName(parameter.getName());
                newParameter.setDescription(parameter.getDescription());
                newParameter.setCommand(newCommand);
                return newParameter;
            }).collect(Collectors.toList());

            newCommand.setParameters(newParameters);
            newCommandDescription.setCommand(newCommand);

            updatedDevice.getCommands().add(newCommandDescription);
        });

        return devicesRepository.save(updatedDevice);
    }

    @Transactional
    public SuccessResponse deleteDevice(@NonNull final String deviceCode) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device.");
        }

        devicesRepository.deleteByDeviceCode(deviceCode);
        return new SuccessResponse(200, "Device was successfully deleted.");
    }

    public List<Device> getAllDevices() {
        final User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().name().equals("ADMIN")) {
            return devicesRepository.findAll();
        }
        return devicesRepository.findByUserId(currentUser.getId());
    }

    public DeviceResponse getAllDevices(@NonNull final int pageNo, @NonNull final int pageSize, @NonNull final String sortBy, @NonNull final String sortDir, @NonNull final String status,
                                        @NonNull final String industryType, @NonNull final String deviceName, @NonNull final String userName, @NonNull final String description, @NonNull final String deviceCode) {
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

    public MonitoringResponse getMonitoringsByDeviceCode(@NonNull final String deviceCode, @NonNull final int pageNo, @NonNull final int pageSize, @NonNull final String sortBy,
                                                         @NonNull final String sortDir,
                                                         @NonNull final MonitoringStatus status, @NonNull final String monitoringCode, @NonNull final String userName, @NonNull final String deviceName,
                                                         @NonNull final String createdAt, @NonNull final String updatedAt) {
        logger.info("Fetching monitorings for deviceCode: {}", deviceCode);

        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());
        final User currentUser = userService.getCurrentUser();

        final LocalDateTime[] createdAtRange = parseDateRange(createdAt);
        final LocalDateTime[] updatedAtRange = parseDateRange(updatedAt);

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

        final List<Monitoring> content = monitorings.getContent();

        return MonitoringResponse.builder()
                .content(content)
                .pageNo(monitorings.getNumber())
                .pageSize(monitorings.getSize())
                .totalElements(monitorings.getTotalElements())
                .totalPages(monitorings.getTotalPages())
                .last(monitorings.isLast())
                .build();
    }

    public Device sendCommand(@NonNull final String deviceCode, @NonNull final CommandRequest commandRequest) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to view this device");
        }
        if (Objects.equals(commandRequest.getOperation(), "Deactivate")) {
            device.setDeviceStatus(DeviceStatus.OFF);
        } else if (Objects.equals(commandRequest.getOperation(), "Activate")) {
            device.setDeviceStatus(DeviceStatus.ON);
        }
        return devicesRepository.save(device);
    }

    private LocalDateTime[] parseDateRange(@NonNull final String dateRange) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (!dateRange.isEmpty()) {
            try {
                if (dateRange.contains("-")) {
                    final String[] dates = dateRange.split("-");
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
        return new LocalDateTime[]{start, end};
    }

    private String generateDeviceCode() {
        final String lastDeviceCode = devicesRepository.findTopByOrderByCreatedAtDesc()
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
