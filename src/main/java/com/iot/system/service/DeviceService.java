package com.iot.system.service;

import com.iot.system.dto.CommandRequest;
import com.iot.system.dto.DeviceRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private final DevicesRepository devicesRepository;

    @Autowired
    private final MonitoringRepository monitoringRepository;

    @Value("${url.environment}")
    private String urlEnvironment;

    private final UserService userService;

    public DeviceService(final DevicesRepository devicesRepository, final MonitoringRepository monitoringRepository,
            final UserService userService) {
        this.devicesRepository = devicesRepository;
        this.monitoringRepository = monitoringRepository;
        this.userService = userService;
    }

    @Transactional
    public Device saveDevice(@NonNull final DeviceRequest deviceRequest) {
        final User currentUser = userService.getCurrentUser();
        final Device device = new Device();

        device.setDeviceCode(generateDeviceCode());
        setBasicDeviceFields(device, deviceRequest);

        final List<CommandDescription> commandDescriptions = deviceRequest.getCommands();
        if (commandDescriptions != null) {
            commandDescriptions.forEach(command -> command.setDevice(device)); // Associa cada comando ao dispositivo
            device.setCommands(commandDescriptions);
        }

        device.setCreatedBy(currentUser);
        device.setUsers(associateUsers(deviceRequest.getUsernames(), currentUser));

        return devicesRepository.save(device);
    }

    @Transactional
    public Device getDeviceByDeviceCode(@NonNull final String deviceCode) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        validateUserAuthorization(device);
        return device;
    }

    @Transactional
    public Device updateDevice(@NonNull final String deviceCode, @NonNull final DeviceRequest deviceRequest) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        validateUserAuthorization(device);

        setBasicDeviceFields(device, deviceRequest);
        device.setUsers(associateUsers(deviceRequest.getUsernames(), userService.getCurrentUser()));
        updateDeviceCommands(device, deviceRequest.getCommands());

        return devicesRepository.save(device);
    }

    @Transactional
    public SuccessResponse deleteDevice(@NonNull final String deviceCode) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        validateUserAuthorization(device);

        devicesRepository.deleteByDeviceCode(deviceCode);
        return new SuccessResponse(200, "Device was successfully deleted.");
    }

    @Transactional
    public List<Device> getAllDevices() {
        final User currentUser = userService.getCurrentUser();

        if (currentUser.getRole().name().equals("ADMIN")) {
            return devicesRepository.findAll();
        }
        return devicesRepository.findByUsers_Id(currentUser.getId());
    }

    @Transactional
    public DeviceResponse getAllDevices(@NonNull final int pageNo, @NonNull final int pageSize,
            @NonNull final String sortBy, @NonNull final String sortDir,
            @NonNull final String deviceStatus, @NonNull final String industryType, @NonNull final String deviceName,
            @NonNull final String userName, @NonNull final String description, @NonNull final String deviceCode) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        final User currentUser = userService.getCurrentUser();

        Specification<Device> spec = createDeviceSpecification(deviceStatus, industryType, deviceName, userName,
                description, deviceCode);
        if (!currentUser.getRole().name().equals("ADMIN")) {
            spec = spec.and(DeviceSpecification.hasUserId(currentUser.getId()));
        }

        final Page<Device> devices = devicesRepository.findAll(spec, pageable);
        return buildDeviceResponse(devices);
    }

    @Transactional
    public MonitoringResponse getMonitoringsByDeviceCode(@NonNull final String deviceCode, @NonNull final int pageNo,
            @NonNull final int pageSize,
            @NonNull final String sortBy, @NonNull final String sortDir, @NonNull final String monitoringStatus,
            @NonNull final String monitoringCode, @NonNull final String userName, @NonNull final String deviceName,
            @NonNull final String createdAt, @NonNull final String updatedAt) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize,
                Sort.Direction.fromString(sortDir).equals(Sort.Direction.ASC) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());

        final Specification<Monitoring> spec = createMonitoringSpecification(deviceCode, monitoringStatus,
                monitoringCode, userName, deviceName, createdAt, updatedAt);
        final Page<Monitoring> monitorings = monitoringRepository.findAll(spec, pageable);
        return buildMonitoringResponse(monitorings);
    }

    @Transactional
    public Device sendCommand(@NonNull final String deviceCode, @NonNull final CommandRequest commandRequest) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        validateUserAuthorization(device);

        if (Objects.equals(commandRequest.getOperation(), "Deactivate")) {
            device.setDeviceStatus(DeviceStatus.OFF);
        } else if (Objects.equals(commandRequest.getOperation(), "Activate")) {
            device.setDeviceStatus(DeviceStatus.ON);
        }

        return devicesRepository.save(device);
    }

    // Métodos private estão abaixo

    private void setBasicDeviceFields(final Device device, final DeviceRequest deviceRequest) {
        device.setDeviceName(deviceRequest.getDeviceName());
        device.setDescription(deviceRequest.getDescription());
        device.setIndustryType(deviceRequest.getIndustryType());
        device.setManufacturer(deviceRequest.getManufacturer());
        device.setUrl(urlEnvironment + "/devices/command/" + device.getDeviceCode());
        device.setDeviceStatus(deviceRequest.getDeviceStatus());
    }

    private List<User> associateUsers(final List<String> usernames, final User currentUser) {
        final List<User> users = new ArrayList<>();
        users.add(currentUser);
        if (usernames != null && !usernames.isEmpty()) {
            users.addAll(getUsers(usernames));
        }
        return users;
    }

    private List<User> getUsers(final List<String> usernames) {
        return userService.findUsersByUsernameList(usernames);
    }

    private void validateUserAuthorization(final Device device) {
        final User currentUser = userService.getCurrentUser();
        final boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        final boolean isUserAuthorized = device.getUsers().stream()
                .anyMatch(user -> user.getId().equals(currentUser.getId()));

        if (!isAdmin && !isUserAuthorized) {
            throw new UnauthorizedException("User not authorized to access this device.");
        }
    }

    private void updateDeviceCommands(final Device device, final List<CommandDescription> commandDescriptions) {
        final Map<Long, CommandDescription> existingCommands = device.getCommands().stream()
                .collect(Collectors.toMap(CommandDescription::getId, command -> command));

        commandDescriptions.forEach(commandDescriptionRequest -> {
            final CommandDescription existingCommand = existingCommands.get(commandDescriptionRequest.getId());
            if (existingCommand != null) {
                updateExistingCommand(existingCommand, commandDescriptionRequest);
            } else {
                device.getCommands().add(createNewCommandDescription(commandDescriptionRequest, device));
            }
        });

        final List<Long> updatedCommandIds = commandDescriptions.stream()
                .map(CommandDescription::getId)
                .collect(Collectors.toList());
        device.getCommands().removeIf(command -> !updatedCommandIds.contains(command.getId()));
    }

    private CommandDescription createNewCommandDescription(final CommandDescription commandDescriptionRequest,
            final Device device) {
        final CommandDescription newCommandDescription = new CommandDescription();
        newCommandDescription.setOperation(commandDescriptionRequest.getOperation());
        newCommandDescription.setDescription(commandDescriptionRequest.getDescription());
        newCommandDescription.setResult(commandDescriptionRequest.getResult());
        newCommandDescription.setFormat(commandDescriptionRequest.getFormat());
        newCommandDescription.setDevice(device);

        newCommandDescription.setCommand(createNewCommand(commandDescriptionRequest.getCommand()));
        return newCommandDescription;
    }

    // Implementação do método createNewCommand
    private Command createNewCommand(final Command commandRequest) {
        final Command newCommand = new Command();
        newCommand.setCommand(commandRequest.getCommand());

        final List<Parameter> newParameters = commandRequest.getParameters().stream()
                .map(param -> {
                    final Parameter newParam = new Parameter();
                    newParam.setName(param.getName());
                    newParam.setDescription(param.getDescription());
                    newParam.setCommand(newCommand);
                    return newParam;
                }).collect(Collectors.toList());

        newCommand.setParameters(newParameters);
        return newCommand;
    }

    private void updateExistingCommand(final CommandDescription existingCommand,
            final CommandDescription commandRequest) {
        existingCommand.setOperation(commandRequest.getOperation());
        existingCommand.setDescription(commandRequest.getDescription());
        existingCommand.setResult(commandRequest.getResult());
        existingCommand.setFormat(commandRequest.getFormat());
        updateCommand(existingCommand.getCommand(), commandRequest.getCommand());
    }

    private void updateCommand(final Command existingCommand, final Command commandRequest) {
        existingCommand.setCommand(commandRequest.getCommand());

        final Map<Long, Parameter> existingParameters = existingCommand.getParameters().stream()
                .collect(Collectors.toMap(Parameter::getId, parameter -> parameter));

        commandRequest.getParameters().forEach(parameterRequest -> {
            final Parameter existingParameter = existingParameters.get(parameterRequest.getId());
            if (existingParameter != null) {
                existingParameter.setName(parameterRequest.getName());
                existingParameter.setDescription(parameterRequest.getDescription());
            } else {
                existingCommand.getParameters().add(createNewParameter(parameterRequest, existingCommand));
            }
        });

        final List<Long> updatedParameterIds = commandRequest.getParameters().stream()
                .map(Parameter::getId)
                .collect(Collectors.toList());
        existingCommand.getParameters().removeIf(parameter -> !updatedParameterIds.contains(parameter.getId()));
    }

    private Parameter createNewParameter(final Parameter parameterRequest, final Command command) {
        final Parameter newParameter = new Parameter();
        newParameter.setName(parameterRequest.getName());
        newParameter.setDescription(parameterRequest.getDescription());
        newParameter.setCommand(command);
        return newParameter;
    }

    private Specification<Device> createDeviceSpecification(final String deviceStatus, final String industryType,
            final String deviceName,
            final String userName, final String description, final String deviceCode) {
        final DeviceStatus statusEnum = parseDeviceStatus(deviceStatus);
        return Specification.where(DeviceSpecification.hasDeviceStatus(statusEnum))
                .and(DeviceSpecification.hasIndustryType(industryType))
                .and(DeviceSpecification.hasCreatedBy(userName))
                .and(DeviceSpecification.hasDeviceName(deviceName))
                .and(DeviceSpecification.hasDescription(description))
                .and(DeviceSpecification.hasDeviceCode(deviceCode));
    }

    private DeviceStatus parseDeviceStatus(final String deviceStatus) {
        if (deviceStatus != null && !deviceStatus.isEmpty()) {
            try {
                return DeviceStatus.valueOf(deviceStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid DeviceStatus value: {}", deviceStatus, e);
                throw new IllegalArgumentException("Invalid DeviceStatus value: " + deviceStatus);
            }
        }
        return null;
    }

    private DeviceResponse buildDeviceResponse(final Page<Device> devices) {
        final List<Device> content = devices.getContent();
        return DeviceResponse.builder()
                .content(content)
                .pageNo(devices.getNumber())
                .pageSize(devices.getSize())
                .totalElements(devices.getTotalElements())
                .totalPages(devices.getTotalPages())
                .last(devices.isLast())
                .build();
    }

    private Specification<Monitoring> createMonitoringSpecification(final String deviceCode,
            final String monitoringStatus, final String monitoringCode,
            final String userName, final String deviceName, final String createdAt, final String updatedAt) {
        final MonitoringStatus statusEnum = parseMonitoringStatus(monitoringStatus);
        final LocalDateTime[] createdAtRange = parseDateRange(createdAt);
        final LocalDateTime[] updatedAtRange = parseDateRange(updatedAt);

        return Specification.where(MonitoringSpecification.hasDeviceCode(deviceCode))
                .and(MonitoringSpecification.hasMonitoringStatus(statusEnum))
                .and(MonitoringSpecification.hasMonitoringCode(monitoringCode))
                .and(MonitoringSpecification.hasUserName(userName))
                .and(MonitoringSpecification.hasDeviceName(deviceName))
                .and(MonitoringSpecification.createdAtAfter(createdAtRange[0]))
                .and(MonitoringSpecification.createdAtBefore(createdAtRange[1]))
                .and(MonitoringSpecification.updatedAtAfter(updatedAtRange[0]))
                .and(MonitoringSpecification.updatedAtBefore(updatedAtRange[1]));
    }

    private MonitoringStatus parseMonitoringStatus(final String monitoringStatus) {
        if (monitoringStatus != null && !monitoringStatus.isEmpty()) {
            try {
                return MonitoringStatus.valueOf(monitoringStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid MonitoringStatus value: {}", monitoringStatus, e);
                throw new IllegalArgumentException("Invalid MonitoringStatus value: " + monitoringStatus);
            }
        }
        return null;
    }

    private MonitoringResponse buildMonitoringResponse(final Page<Monitoring> monitorings) {
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

    private LocalDateTime[] parseDateRange(@NonNull final String dateRange) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (dateRange != null && !dateRange.isEmpty()) {
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
        return new LocalDateTime[] { start, end };
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
