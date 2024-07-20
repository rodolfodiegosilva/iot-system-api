package com.iot.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.system.dto.CommandRequest;
import com.iot.system.dto.MonitoringResponse;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.model.Device;
import com.iot.system.model.Monitoring;
import com.iot.system.model.MonitoringStatus;
import com.iot.system.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Device mockDevice;

    @BeforeEach
    void setUp() {
        mockDevice = new Device();
        mockDevice.setDeviceCode("DVC00002");
        mockDevice.setDeviceName("Predefined Device");
        mockDevice.setDescription("Predefined Description");
        mockDevice.setIndustryType("Test Industry");
        mockDevice.setManufacturer("Test Manufacturer");
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testGetDeviceByDeviceCode() throws Exception {
        when(deviceService.getDeviceByDeviceCode("DVC00002")).thenReturn(mockDevice);

        mockMvc.perform(get("/devices/DVC00002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceCode", is("DVC00002")))
                .andExpect(jsonPath("$.deviceName", is("Predefined Device")));
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testGetDeviceByDeviceCode_NotFound() throws Exception {
        when(deviceService.getDeviceByDeviceCode("DVC99999")).thenThrow(new ResourceNotFoundException("Device not found"));

        mockMvc.perform(get("/devices/DVC99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testGetAllDevices() throws Exception {
        when(deviceService.getAllDevices()).thenReturn(Collections.singletonList(mockDevice));

        mockMvc.perform(get("/devices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deviceCode", is("DVC00002")))
                .andExpect(jsonPath("$[0].deviceName", is("Predefined Device")));
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testAddDevice() throws Exception {
        when(deviceService.saveDevice(any(Device.class))).thenReturn(mockDevice);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDevice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceCode", is("DVC00002")))
                .andExpect(jsonPath("$.deviceName", is("Predefined Device")));
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testUpdateDevice() throws Exception {
        when(deviceService.updateDevice(eq("DVC00002"), any(Device.class))).thenReturn(mockDevice);

        mockMvc.perform(put("/devices/DVC00002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDevice)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceCode", is("DVC00002")))
                .andExpect(jsonPath("$.deviceName", is("Predefined Device")));
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testDeleteDevice() throws Exception {
        when(deviceService.deleteDevice("DVC00002")).thenReturn(new SuccessResponse(200, "Device was successfully deleted."));

        mockMvc.perform(delete("/devices/DVC00002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Device was successfully deleted.")));
    }

    @Test
    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testSendCommand() throws Exception {
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setOperation("Activate");

        when(deviceService.sendCommand(eq("DVC00002"), any(CommandRequest.class))).thenReturn(mockDevice);

        mockMvc.perform(post("/devices/command/DVC00002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commandRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceCode", is("DVC00002")))
                .andExpect(jsonPath("$.deviceName", is("Predefined Device")));
    }


    @WithMockUser(username = "usertest1", roles = {"USER"})
    public void testGetMonitoringsByDeviceCode() throws Exception {
        List<Monitoring> monitorings = Collections.emptyList();
        MonitoringResponse monitoringResponse = MonitoringResponse.builder()
                .content(monitorings)
                .build();

        when(deviceService.getMonitoringsByDeviceCode(eq("DVC00002"), anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(monitoringResponse);

        mockMvc.perform(get("/devices/DVC00002/monitorings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
