package com.iot.system.service;

import com.iot.system.model.Device;
import com.iot.system.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDevices() {
        Device device1 = new Device();
        device1.setName("Device1");

        Device device2 = new Device();
        device2.setName("Device2");

        when(deviceRepository.findAll()).thenReturn(Arrays.asList(device1, device2));

        List<Device> devices = deviceService.getAllDevices();
        assertEquals(2, devices.size());
        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    public void testGetDeviceById() {
        Device device = new Device();
        device.setId(1L);
        device.setName("Device1");

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));

        Device foundDevice = deviceService.getDeviceById(1L);
        assertEquals("Device1", foundDevice.getName());
        verify(deviceRepository, times(1)).findById(1L);
    }

    @Test
    public void testSaveDevice() {
        Device device = new Device();
        device.setName("Device1");

        when(deviceRepository.save(device)).thenReturn(device);

        Device savedDevice = deviceService.saveDevice(device);
        assertEquals("Device1", savedDevice.getName());
        verify(deviceRepository, times(1)).save(device);
    }

    @Test
    public void testDeleteDevice() {
        deviceService.deleteDevice(1L);
        verify(deviceRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateDevice() {
        Device existingDevice = new Device();
        existingDevice.setId(1L);
        existingDevice.setName("OldName");
        existingDevice.setDescription("OldDescription");

        Device updatedDevice = new Device();
        updatedDevice.setName("NewName");
        updatedDevice.setDescription("NewDescription");

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(existingDevice));
        when(deviceRepository.save(existingDevice)).thenReturn(existingDevice);

        Device result = deviceService.updateDevice(1L, updatedDevice);

        assertEquals("NewName", result.getName());
        assertEquals("NewDescription", result.getDescription());
        verify(deviceRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).save(existingDevice);
    }

}
