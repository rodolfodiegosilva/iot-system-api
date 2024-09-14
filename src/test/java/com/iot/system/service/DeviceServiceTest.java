package com.iot.system.service;

import com.iot.system.dto.DeviceRequest;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.exception.UnauthorizedException;
import com.iot.system.model.Device;
import com.iot.system.model.DeviceStatus;
import com.iot.system.user.Role;
import com.iot.system.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = "/test-data.sql")
class DeviceServiceTest {

    @MockBean
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("usertest1@example.com");
        mockUser.setRole(Role.USER);
        mockUser.setName("User Test 1");
        mockUser.setUsername("usertest1");
        when(userService.getCurrentUser()).thenReturn(mockUser);
    }


    void testSaveDevice() {
        DeviceRequest device = new DeviceRequest();
        device.setDeviceName("Test Device");
        device.setDescription("Test Description");
        device.setIndustryType("Test Industry");
        device.setManufacturer("Test Manufacturer");

        Device result = deviceService.saveDevice(device);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Device", result.getDeviceName());
        assertEquals("Test Description", result.getDescription());
        assertEquals("Test Industry", result.getIndustryType());
        assertEquals("Test Manufacturer", result.getManufacturer());
        assertNotNull(result.getUsers());
        assertTrue(result.getDeviceCode().startsWith("DVC"));
        assertTrue(result.getUrl().contains(result.getDeviceCode()));
    }


    void testGetDeviceByDeviceCode() {
        Device retrievedDevice = deviceService.getDeviceByDeviceCode("DVC00002");

        assertNotNull(retrievedDevice);
        assertEquals("Predefined Device", retrievedDevice.getDeviceName());
        assertEquals("Predefined Description", retrievedDevice.getDescription());
        assertEquals("Test Industry", retrievedDevice.getIndustryType());
        assertEquals("Test Manufacturer", retrievedDevice.getManufacturer());
        assertNotNull(retrievedDevice.getUsers());
    }


    void testGetDeviceByDeviceCodeUnauthorized() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);
        unauthorizedUser.setEmail("unauthorized@example.com");
        unauthorizedUser.setRole(Role.USER);
        unauthorizedUser.setName("Unauthorized User");
        unauthorizedUser.setUsername("unauthorized");
        when(userService.getCurrentUser()).thenReturn(unauthorizedUser);

        assertThrows(UnauthorizedException.class, () -> {
            deviceService.getDeviceByDeviceCode("DVC00002");
        });
    }


    void testGetDeviceByDeviceCodeDeviceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            deviceService.getDeviceByDeviceCode("DVC999999");
        });
    }


    void testUpdateDevice() {
        Device deviceRequest = new Device();
        deviceRequest.setDeviceName("Updated Device");
        deviceRequest.setDescription("Updated Description");
        deviceRequest.setIndustryType("Updated Industry");
        deviceRequest.setManufacturer("Updated Manufacturer");
        deviceRequest.setDeviceStatus(DeviceStatus.ON);
        deviceRequest.setCommands(new ArrayList<>());

        Device updatedDevice = deviceService.updateDevice("DVC00002", deviceRequest);

        assertNotNull(updatedDevice);
        assertEquals("Updated Device", updatedDevice.getDeviceName());
        assertEquals("Updated Description", updatedDevice.getDescription());
        assertEquals("Updated Industry", updatedDevice.getIndustryType());
        assertEquals("Updated Manufacturer", updatedDevice.getManufacturer());
        assertEquals(DeviceStatus.ON, updatedDevice.getDeviceStatus());
    }


    void testDeleteDevice() {
        SuccessResponse response = deviceService.deleteDevice("DVC00002");
        assertEquals(200, response.getStatus());
        assertEquals("Device was successfully deleted.", response.getMessage());
    }


    void testUpdateDeviceUnauthorized() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);
        unauthorizedUser.setEmail("unauthorized@example.com");
        unauthorizedUser.setRole(Role.USER);
        unauthorizedUser.setName("Unauthorized User");
        unauthorizedUser.setUsername("unauthorized");
        unauthorizedUser.setPassword("Abc@123");
        when(userService.getCurrentUser()).thenReturn(unauthorizedUser);

        Device deviceRequest = new Device();
        deviceRequest.setDeviceName("Updated Device");

        assertThrows(UnauthorizedException.class, () -> {
            deviceService.updateDevice("DVC00002", deviceRequest);
        });
    }


    void testDeleteDeviceUnauthorized() {
        User unauthorizedUser = new User();
        unauthorizedUser.setId(3L);
        unauthorizedUser.setEmail("unauthorized@example.com");
        unauthorizedUser.setRole(Role.USER);
        unauthorizedUser.setName("Unauthorized User");
        unauthorizedUser.setUsername("unauthorized");
        when(userService.getCurrentUser()).thenReturn(unauthorizedUser);

        assertThrows(UnauthorizedException.class, () -> {
            deviceService.deleteDevice("DVC00002");
        });
    }
}
