package com.iot.system.service;

import com.iot.system.dto.MonitoringRequest;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.exception.UnauthorizedException;
import com.iot.system.model.Device;
import com.iot.system.model.Monitoring;
import com.iot.system.model.MonitoringStatus;
import com.iot.system.repository.DevicesRepository;
import com.iot.system.repository.MonitoringRepository;
import com.iot.system.repository.MonitoringSpecification;
import com.iot.system.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MonitoringService {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);


    private final MonitoringRepository monitoringRepository;
    private final DevicesRepository devicesRepository;
    private final UserService userService;

    public List<Monitoring> getAllMonitorings() {
        final User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().name().equals("ADMIN")) {
            return monitoringRepository.findAll();
        }
        return monitoringRepository.findByUsers_Id(currentUser.getId());
    }

    public List<Monitoring> createMonitoring(final List<MonitoringRequest> monitoringRequests) {
        final List<Monitoring> monitoringToAdd = new ArrayList<>();

        synchronized (this) {
            final String lastMonitoringCode = monitoringRepository.findTopByOrderByCreatedAtDesc()
                    .map(Monitoring::getMonitoringCode)
                    .orElse("MON00000");
            int lastNumber = Integer.parseInt(lastMonitoringCode.substring(3));

            for (final MonitoringRequest request : monitoringRequests) {

                final Device device = devicesRepository.findByDeviceCode(request.getDeviceCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Device with code " + request.getDeviceCode() + " not found"));

                Optional<Monitoring> existingMonitoring = monitoringRepository.findByDevice(device);
                if (existingMonitoring.isPresent()) {
                    throw new IllegalArgumentException("Monitoring already exists for device " + device.getDeviceCode());
                }

                if (request.getDescription() == null || request.getDescription().isEmpty()) {
                    throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
                }

                final String newMonitoringCode = generateMonitoringCode(lastNumber);
                lastNumber = Integer.parseInt(newMonitoringCode.substring(3)) + 1;

                final Monitoring monitoring = new Monitoring();
                monitoring.setMonitoringCode(newMonitoringCode);
                monitoring.setDevice(device);
                monitoring.setMonitoringStatus(request.getMonitoringStatus());
                monitoring.setDescription(request.getDescription());
                monitoring.setCreatedAt(LocalDateTime.now());
                monitoring.setUpdatedAt(LocalDateTime.now());

                User currentUser = userService.getCurrentUser();
                monitoring.setCreatedBy(currentUser);

                if (!isUserAuthorizedToCreateMonitoring(currentUser, device)) {
                    throw new UnauthorizedException("User not authorized to create monitoring for device " + request.getDeviceCode());
                }

                if (device.getUsers() != null) {
                    List<User> copiedUsers = new ArrayList<>(device.getUsers());
                    monitoring.setUsers(copiedUsers);
                }

                monitoringToAdd.add(monitoring);
            }
        }

        return monitoringRepository.saveAll(monitoringToAdd);
    }


    private boolean isUserAuthorizedToCreateMonitoring(User currentUser, Device device) {
        if (device.getCreatedBy().getId().equals(currentUser.getId())) {
            return true;
        }

        if (device.getUsers().stream().anyMatch(user -> user.getId().equals(currentUser.getId()))) {
            return true;
        }

        return currentUser.getRole().name().equals("ADMIN");
    }


    public Monitoring getMonitoringByCode(final String monitoringCode) {
        final Monitoring monitoring = monitoringRepository.findByMonitoringCode(monitoringCode)
                .orElseThrow(() -> new ResourceNotFoundException("Monitoring not found"));
        final User currentUser = userService.getCurrentUser();
        if (!monitoring.getCreatedBy().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to view this monitoring");
        }
        return monitoring;
    }

    public MonitoringResponse getAllMonitoring(final int pageNo, final int pageSize, final String sortBy, final String sortDir,
                                               final MonitoringStatus monitoringStatus, final String deviceCode, final String monitoringCode,
                                               final String userName, final String deviceName, final String createdAt, final String updatedAt) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        final User currentUser = userService.getCurrentUser();

        final LocalDateTime[] createdAtRange = parseDateRange(createdAt);
        final LocalDateTime[] updatedAtRange = parseDateRange(updatedAt);

        Specification<Monitoring> spec = Specification.where(MonitoringSpecification.hasMonitoringStatus(monitoringStatus))
                .and(MonitoringSpecification.hasDeviceCode(deviceCode))
                .and(MonitoringSpecification.hasMonitoringCode(monitoringCode))
                .and(MonitoringSpecification.hasUserName(userName))
                .and(MonitoringSpecification.hasDeviceName(deviceName))
                .and(MonitoringSpecification.createdAtAfter(createdAtRange[0]))
                .and(MonitoringSpecification.createdAtBefore(createdAtRange[1]))
                .and(MonitoringSpecification.updatedAtAfter(updatedAtRange[0]))
                .and(MonitoringSpecification.updatedAtBefore(updatedAtRange[1]));

        if (!currentUser.getRole().name().equals("ADMIN")) {
            spec = spec.and(MonitoringSpecification.hasUserId(currentUser.getId()));
        }

        final Page<Monitoring> monitorings = monitoringRepository.findAll(spec, pageable);
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


    private LocalDateTime[] parseDateRange(final String dateRange) {
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
            } catch (final DateTimeParseException e) {
                // Handle the exception as needed
            }
        }
        return new LocalDateTime[]{start, end};
    }

    public Monitoring updateMonitoring(final String monitoringCode, final MonitoringRequest monitoringRequest) {
        final Monitoring monitoring = monitoringRepository.findByMonitoringCode(monitoringCode)
                .orElseThrow(() -> new ResourceNotFoundException("Monitoring with code " + monitoringCode + " not found"));

        final User currentUser = userService.getCurrentUser();
        if (!monitoring.getCreatedBy().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to update this monitoring");
        }

        if (!monitoring.getDevice().getDeviceCode().equals(monitoringRequest.getDeviceCode())) {
            final Device device = devicesRepository.findByDeviceCode(monitoringRequest.getDeviceCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Device with code " + monitoringRequest.getDeviceCode() + " not found"));
            monitoring.setDevice(device);
        }

        monitoring.setMonitoringStatus(monitoringRequest.getMonitoringStatus());
        monitoring.setDescription(monitoringRequest.getDescription());
        monitoring.setUpdatedAt(LocalDateTime.now());

        return monitoringRepository.save(monitoring);
    }

    @Transactional
    public SuccessResponse deleteMonitoring(final String monitoringCode) {

        final Monitoring monitoring = monitoringRepository.findByMonitoringCode(monitoringCode)
                .orElseThrow(() -> new ResourceNotFoundException("Monitoring with code " + monitoringCode + " not found"));

        final User currentUser = userService.getCurrentUser();

        if (!monitoring.getCreatedBy().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this monitoring");
        }

        try {
            monitoringRepository.deleteByMonitoringCode(monitoringCode);
            monitoringRepository.flush();

            boolean exists = monitoringRepository.existsByMonitoringCode(monitoringCode);
            if (exists) {
                throw new RuntimeException("Failed to delete Monitoring with code: " + monitoringCode);
            }
            return new SuccessResponse(200, "Monitoring was successfully deleted.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete Monitoring with code: " + monitoringCode, e);
        }
    }

    public void deleteMultipleMonitoring(final List<String> monitoringCodes) {
        for (final String monitoringCode : monitoringCodes) {
            deleteMonitoring(monitoringCode);
        }
    }

    @Transactional
    public String generateMonitoringCode(int lastNumber) {
        String newMonitoringCode;
        boolean exists;

        do {
            lastNumber++;
            newMonitoringCode = "MON" + String.format("%05d", lastNumber);
            exists = monitoringRepository.existsByMonitoringCode(newMonitoringCode);
        } while (exists);

        return newMonitoringCode;
    }
}
