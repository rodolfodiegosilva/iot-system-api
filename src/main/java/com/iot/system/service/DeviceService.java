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


    public DeviceService(final DevicesRepository devicesRepository, final MonitoringRepository monitoringRepository, final UserService userService) {
        this.devicesRepository = devicesRepository;
        this.monitoringRepository = monitoringRepository;
        this.userService = userService;
    }

    @Transactional
    public Device saveDevice(@NonNull final DeviceRequest deviceRequest) {

        final User currentUser = userService.getCurrentUser();

        Device device = new Device();
        String generatedDeviceCode = generateDeviceCode();
        device.setDeviceCode(generatedDeviceCode);
        device.setDeviceName(deviceRequest.getDeviceName());
        device.setDescription(deviceRequest.getDescription());
        device.setIndustryType(deviceRequest.getIndustryType());
        device.setManufacturer(deviceRequest.getManufacturer());
        String deviceUrl = urlEnvironment + "/devices/command/" + device.getDeviceCode();
        device.setUrl(deviceUrl);
        device.setDeviceStatus(deviceRequest.getDeviceStatus());

        List<CommandDescription> commandDescriptions = deviceRequest.getCommands();

        // Associe cada comando ao dispositivo antes de salvar
        if (commandDescriptions != null) {
            for (CommandDescription command : commandDescriptions) {
                command.setDevice(device); // Associa o comando ao dispositivo
            }
            device.setCommands(commandDescriptions);
        }

        device.setCreatedBy(currentUser);

        List<User> users = new ArrayList<>();
        users.add(currentUser);

        if (deviceRequest.getUsernames() != null && !deviceRequest.getUsernames().isEmpty()) {
            List<User> additionalUsers = userService.findUsersByUsernameList(deviceRequest.getUsernames());
            users.addAll(additionalUsers);
        }

        device.setUsers(users);

        // Salve o dispositivo, o JPA cuidará de persistir as entidades relacionadas
        return devicesRepository.save(device);
    }



    @Transactional
    public Device getDeviceByDeviceCode(@NonNull final String deviceCode) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (device.getUsers().stream().noneMatch(user -> user.getId().equals(currentUser.getId()))
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device.");
        }
        return device;
    }

    @Transactional
    public Device updateDevice(@NonNull final String deviceCode, @NonNull final Device deviceRequest) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (device.getUsers().stream().noneMatch(user -> user.getId().equals(currentUser.getId()))
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device.");
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
        if (device.getUsers().stream().noneMatch(user -> user.getId().equals(currentUser.getId()))
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device.");
        }

        devicesRepository.deleteByDeviceCode(deviceCode);
        return new SuccessResponse(200, "Device was successfully deleted.");
    }

    @Transactional
    public List<Device> getAllDevices() {
        final User currentUser = userService.getCurrentUser();

        // Se o usuário for ADMIN, retorna todos os dispositivos
        if (currentUser.getRole().name().equals("ADMIN")) {
            return devicesRepository.findAll();
        }

        // Para outros usuários, retorna dispositivos associados ao ID do usuário
        return devicesRepository.findByUsers_Id(currentUser.getId());
    }


    @Transactional
    public DeviceResponse getAllDevices(@NonNull final int pageNo, @NonNull final int pageSize, @NonNull final String sortBy, @NonNull final String sortDir, @NonNull final String deviceStatus,
                                        @NonNull final String industryType, @NonNull final String deviceName, @NonNull final String userName, @NonNull final String description, @NonNull final String deviceCode) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        final User currentUser = userService.getCurrentUser();

        DeviceStatus statusEnum = null;
        if (deviceStatus != null && !deviceStatus.isEmpty()) {
            try {
                statusEnum = DeviceStatus.valueOf(deviceStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid DeviceStatus value: {}", deviceStatus, e);
                throw new IllegalArgumentException("Invalid DeviceStatus value: " + deviceStatus);
            }
        }

        Specification<Device> spec = Specification.where(DeviceSpecification.hasDeviceStatus(statusEnum))
                .and(DeviceSpecification.hasIndustryType(industryType))
                .and(DeviceSpecification.hasCreatedBy(userName))
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

    @Transactional
    public MonitoringResponse getMonitoringsByDeviceCode(@NonNull final String deviceCode, @NonNull final int pageNo, @NonNull final int pageSize, @NonNull final String sortBy,
                                                         @NonNull final String sortDir,
                                                         @NonNull final String monitoringStatus, @NonNull final String monitoringCode, @NonNull final String userName, @NonNull final String deviceName,
                                                         @NonNull final String createdAt, @NonNull final String updatedAt) {
        logger.info("Fetching monitorings for deviceCode: {}", deviceCode);

        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());
        final User currentUser = userService.getCurrentUser();

        final LocalDateTime[] createdAtRange = parseDateRange(createdAt);
        final LocalDateTime[] updatedAtRange = parseDateRange(updatedAt);

        MonitoringStatus statusEnum = null;
        if (monitoringStatus != null && !monitoringStatus.isEmpty()) {
            try {
                statusEnum = MonitoringStatus.valueOf(monitoringStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid MonitoringStatus value: {}", monitoringStatus, e);
                throw new IllegalArgumentException("Invalid MonitoringStatus value: " + monitoringStatus);
            }
        }

        Specification<Monitoring> spec = Specification.where(MonitoringSpecification.hasDeviceCode(deviceCode))
                .and(MonitoringSpecification.hasMonitoringStatus(statusEnum))
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

    @Transactional
    public Device sendCommand(@NonNull final String deviceCode, @NonNull final CommandRequest commandRequest) {
        final Device device = devicesRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        final User currentUser = userService.getCurrentUser();
        if (device.getUsers().stream().noneMatch(user -> user.getId().equals(currentUser.getId()))
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device.");
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
