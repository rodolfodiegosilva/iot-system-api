package com.iot.system.service;

import com.iot.system.dto.DeviceDTO;
import com.iot.system.dto.UserDTO;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.UnauthorizedException;
import com.iot.system.model.Device;
import com.iot.system.repository.DeviceRepository;
import com.iot.system.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    private final UserService userService;

    public DeviceService(UserService userService) {
        this.userService = userService;
    }

    public List<DeviceDTO> getAllDevices() {
        User currentUser = userService.getCurrentUser();
        List<Device> devices;
        if (currentUser.getRole().name().equals("ADMIN")) {
            devices = deviceRepository.findAll();
        } else {
            devices = deviceRepository.findByUserId(currentUser.getId());
        }
        return devices.stream()
                .map(this::convertToDeviceDTO)
                .collect(Collectors.toList());
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
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        return userDTO;
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