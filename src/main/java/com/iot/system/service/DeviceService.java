package com.iot.system.service;

import com.iot.system.dto.DeviceDTO;
import com.iot.system.dto.DeviceResponse;
import com.iot.system.dto.UserDTO;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.UnauthorizedException;
import com.iot.system.model.Device;
import com.iot.system.repository.DeviceRepository;
import com.iot.system.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    private final UserService userService;

    public DeviceService(UserService userService) {
        this.userService = userService;
    }

    public DeviceResponse getAllDevices(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<Device> devices = deviceRepository.findAll(pageable);
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

    public DeviceDTO getDeviceByDeviceCode(String deviceCode) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to view this device");
        }
        return convertToDeviceDTO(device);
    }

    public DeviceDTO saveDevice(Device device) {
        User currentUser = userService.getCurrentUser();
        device.setUser(currentUser);
        device.setDeviceCode(generateDeviceCode());
        Device savedDevice = deviceRepository.save(device);
        return convertToDeviceDTO(savedDevice);
    }

    public DeviceDTO updateDevice(String deviceCode, Device deviceDetails) {
        Device device = deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to update this device");
        }
        device.setName(deviceDetails.getName());
        device.setDescription(deviceDetails.getDescription());
        device.setStatus(deviceDetails.getStatus());
        Device updatedDevice = deviceRepository.save(device);
        return convertToDeviceDTO(updatedDevice);
    }

    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        User currentUser = userService.getCurrentUser();
        if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("User not authorized to delete this device");
        }
        deviceRepository.deleteById(id);
    }

    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().toString()).build();

    }

    private DeviceDTO convertToDeviceDTO(Device device) {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setId(device.getId());
        deviceDTO.setName(device.getName());
        deviceDTO.setDescription(device.getDescription());
        deviceDTO.setStatus(device.getStatus());
        deviceDTO.setDeviceCode(device.getDeviceCode());

        User user = device.getUser();
        if (user != null) {
            UserDTO userDTO = convertToUserDTO(user);
            deviceDTO.setUser(userDTO);
        }

        return deviceDTO;
    }

    private String generateDeviceCode() {
        String lastDeviceCode = deviceRepository.findTopByOrderByCreatedAtDesc()
                .map(Device::getDeviceCode)
                .orElse("ABC0000");

        int lastNumber = Integer.parseInt(lastDeviceCode.substring(3));
        String newDeviceCode;

        do {
            lastNumber++;
            newDeviceCode = "DVC" + String.format("%04d", lastNumber);
        } while (deviceRepository.existsByDeviceCode(newDeviceCode));

        return newDeviceCode;
    }
}