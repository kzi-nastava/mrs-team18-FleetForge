package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.reports.ReportsRideDTO;
import com.team18.FleetForge.dto.ride.reports.UserDataReportResponseDTO;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.rides.RideService;
import com.team18.FleetForge.service.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final RideService rideService;
    private final UserService userService;

    @PreAuthorize("hasRole('DRIVER')||hasRole('PASSENGER')")
    @GetMapping("/logged-user/report-data")
    public ResponseEntity<UserDataReportResponseDTO> getDataForLoggedUser(@RequestParam(required = true) LocalDate fromDate, @RequestParam(required = true) LocalDate toDate){
        UserDataReportResponseDTO response= new UserDataReportResponseDTO();
        response.setDataByDay(toDTO(rideService.findRidesForLoggedUserForGivenDateRange(fromDate, toDate)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-by-user/report-data")
    public ResponseEntity<UserDataReportResponseDTO> getDataForUser(@RequestParam(required = true) LocalDate fromDate, @RequestParam(required = true) LocalDate toDate,
                                                                    @RequestParam(required = true) String email){
        UserDataReportResponseDTO response= new UserDataReportResponseDTO();

        Optional<User> user= userService.getUserByEmail(email);
        user.ifPresent(value -> response.setDataByDay(toDTO(rideService.findRidesForUserForGivenDateRange(fromDate, toDate, value.getId()))));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/report-data")
    public ResponseEntity<UserDataReportResponseDTO> getDataForReport(@RequestParam(required = true) LocalDate fromDate, @RequestParam(required = true) LocalDate toDate){
        UserDataReportResponseDTO response= new UserDataReportResponseDTO();
        response.setDataByDay(toDTO(rideService.findRidesForGivenDateRange(fromDate, toDate)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Map<LocalDate, List<ReportsRideDTO>> toDTO(Map<LocalDate, List<Ride>> map) {
        Map<LocalDate, List<ReportsRideDTO>> result = new LinkedHashMap<>();
        map.forEach((date, rides) -> result.put(date, rides.stream()
                .map(r -> new ReportsRideDTO(r.getId(), r.getTotalDistance(), r.getTotalCost()))
                .collect(Collectors.toList())));
        return result;
    }
}
