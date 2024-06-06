package com.iot.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.system.model.Device;
import com.iot.system.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDevices() throws Exception {
        Device device1 = new Device();
        device1.setName("Device1");

        Device device2 = new Device();
        device2.setName("Device2");

        List<Device> devices = Arrays.asList(device1, device2);

        when(deviceService.getAllDevices()).thenReturn(devices);

        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Device1"))
                .andExpect(jsonPath("$[1].name").value("Device2"));

        verify(deviceService, times(1)).getAllDevices();
    }

    @Test
    public void testGetDeviceById() throws Exception {
        Device device = new Device();
        device.setId(1L);
        device.setName("Device1");

        when(deviceService.getDeviceById(1L)).thenReturn(device);

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"));

        verify(deviceService, times(1)).getDeviceById(1L);
    }

    @Test
    public void testCreateDevice() throws Exception {
        Device device = new Device();
        device.setName("Device1");

        when(deviceService.saveDevice(any(Device.class))).thenReturn(device);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Device1"));

        verify(deviceService, times(1)).saveDevice(any(Device.class));
    }

    @Test
    public void testUpdateDevice() throws Exception {
        Device device = new Device();
        device.setId(1L);
        device.setName("UpdatedDevice");

        when(deviceService.updateDevice(eq(1L), any(Device.class))).thenReturn(device);

        mockMvc.perform(put("/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedDevice"));

        verify(deviceService, times(1)).updateDevice(eq(1L), any(Device.class));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        mockMvc.perform(delete("/devices/1"))
                .andExpect(status().isOk());

        verify(deviceService, times(1)).deleteDevice(1L);
    }

    @Test
    public void testGetDeviceById_NotFound() throws Exception {
        when(deviceService.getDeviceById(1L)).thenReturn(null);

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isNotFound());

        verify(deviceService, times(1)).getDeviceById(1L);
    }

    @Test
    public void testUpdateDevice_NotFound() throws Exception {
        Device device = new Device();
        device.setName("UpdatedDevice");

        when(deviceService.updateDevice(eq(1L), any(Device.class))).thenReturn(null);

        mockMvc.perform(put("/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(device)))
                .andExpect(status().isNotFound());

        verify(deviceService, times(1)).updateDevice(eq(1L), any(Device.class));
    }
}
