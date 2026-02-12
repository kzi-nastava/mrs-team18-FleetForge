package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.exception.ride.RideNotActiveException;
import com.team18.FleetForge.service.impl.CustomUserDetailsService;
import com.team18.FleetForge.util.JwtTokenUtils;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.exception.ride.RideNotFoundException;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.service.rides.RideFinishService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RideFinishController.class)
@DisplayName("RideFinishController Integration Tests")
class RideFinishControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RideFinishService rideFinishService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("200 OK successful")
    @WithMockUser(roles = "DRIVER")
    void finishRideSuccessfulRequest() throws Exception {
        FinishRideResponseDTO response = FinishRideResponseDTO.builder()
                .rideId(1L)
                .status(RideStatus.COMPLETED)
                .endTime(LocalDateTime.now())
                .totalCost(2000.0)
                .message("Ride completed successfully")
                .driverAvailable(true)
                .build();

        when(rideFinishService.finishRide(any())).thenReturn(response);

        mockMvc.perform(put("/api/rides/1/finish")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideId").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.totalCost").value(2000.0))
                .andExpect(jsonPath("$.driverAvailable").value(true));

        verify(rideFinishService, times(1)).finishRide(any());
    }

    @Test
    @DisplayName("400 ride not found")
    @WithMockUser(roles = "DRIVER")
    void finishRideNotFound() throws Exception {
        when(rideFinishService.finishRide(any()))
                .thenThrow(new RideNotFoundException("Ride not found with id: 999"));

        mockMvc.perform(put("/api/rides/999/finish")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Ride not found")));
    }

    @Test
    @DisplayName("400 ride not active")
    @WithMockUser(roles = "DRIVER")
    void finishRideNotActive() throws Exception {
        when(rideFinishService.finishRide(any()))
                .thenThrow(new RideNotActiveException("Ride not active with id: 3"));

        mockMvc.perform(put("/api/rides/3/finish")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Ride not active")));
    }

    @Test
    @DisplayName("Not authenticated")
    void finishRideNotAuthenticated() throws Exception {
        mockMvc.perform(put("/api/rides/1/finish")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(rideFinishService, never()).finishRide(any());
    }

}