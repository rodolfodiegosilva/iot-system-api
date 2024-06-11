package com.iot.system.service;

import com.iot.system.dto.DeviceDTO;
import com.iot.system.dto.UserDTO;
import com.iot.system.model.Device;
import com.iot.system.repository.DeviceRepository;
import com.iot.system.repository.UserRepository;
import com.iot.system.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<DeviceDTO> getAllDevices() {
        User currentUser = getCurrentUser();
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

    public DeviceDTO getDeviceById(Long id) {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device != null) {
            User currentUser = getCurrentUser();
            if (device.getUser().getId().equals(currentUser.getId()) || currentUser.getRole().name().equals("ADMIN")) {
                return convertToDeviceDTO(device);
            }
        }
        return null;
    }

    public DeviceDTO saveDevice(Device device) {
        User currentUser = getCurrentUser();
        device.setUser(currentUser);
        Device savedDevice = deviceRepository.save(device);
        return convertToDeviceDTO(savedDevice);
    }

    public DeviceDTO updateDevice(Long id, Device deviceDetails) throws IllegalAccessException {
        Device device = deviceRepository.findById(id).orElse(null);
        if (device != null) {
            User currentUser = getCurrentUser();
            if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
                throw new IllegalAccessException("User not authorized to update this device");
            }
            device.setName(deviceDetails.getName());
            device.setDescription(deviceDetails.getDescription());
            Device updatedDevice = deviceRepository.save(device);
            return convertToDeviceDTO(updatedDevice);
        }
        return null;
    }

    public void deleteDevice(Long id) throws IllegalAccessException {
        Device device = deviceRepository.findById(id).orElse(null);
        User currentUser = getCurrentUser();
        if (device != null) {
            if (!device.getUser().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
                throw new IllegalAccessException("User not authorized to delete this device");
            }
            deviceRepository.deleteById(id);
        }
    }

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        return userOptional.orElseThrow(() -> new IllegalStateException("User not found"));
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

        User user = device.getUser();
        if (user != null) {
            UserDTO userDTO = convertToUserDTO(user);
            deviceDTO.setUser(userDTO);
        }

        return deviceDTO;
    }
}