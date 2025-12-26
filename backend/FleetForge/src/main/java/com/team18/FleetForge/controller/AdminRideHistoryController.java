package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.*;
import com.team18.FleetForge.model.GeoPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rides")
public class AdminRideHistoryController {

    /**
     * GET /api/admin/rides/users/{userId}
     * Filters:
     *  - from, to (date range)
     *  - sortBy (any field)
     *  - direction (asc, desc)
     */
    @GetMapping(
            value = "/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<AdminRideHistoryItemDTO>> getRideHistoryForUser(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        List<AdminRideHistoryItemDTO> history = List.of(
                AdminRideHistoryItemDTO.builder()
                        .rideId(1L)
                        .startTime(LocalDateTime.now().minusDays(1))
                        .endTime(LocalDateTime.now().minusDays(1).plusMinutes(18))
                        .startAddress("Bulevar Oslobodjenja 1")
                        .destinationAddress("Zmaj Jovina 12")
                        .driver(UserSummaryDTO.builder()
                                .id(10L)
                                .firstName("Marko")
                                .lastName("Markovic")
                                .build())
                        .passenger(UserSummaryDTO.builder()
                                .id(20L)
                                .firstName("Petar")
                                .lastName("Petrovic")
                                .build())
                        .build(),

                AdminRideHistoryItemDTO.builder()
                        .rideId(2L)
                        .startTime(LocalDateTime.now().minusHours(2))
                        .endTime(LocalDateTime.now().minusHours(1))
                        .startAddress("Futoška 10")
                        .destinationAddress("Dunavska 5")
                        .driver(UserSummaryDTO.builder()
                                .id(11L)
                                .firstName("Jovan")
                                .lastName("Jovanovic")
                                .build())
                        .passenger(UserSummaryDTO.builder()
                                .id(21L)
                                .firstName("Ana")
                                .lastName("Anic")
                                .build())
                        .build()
        );

        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    /**
     * GET /api/admin/rides/{rideId}
     * Detailed ride view
     */
    @GetMapping(
            value = "/{rideId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDetailsDTO> getRideDetails(
            @PathVariable Long rideId
    ) {
        RideDetailsDTO response = RideDetailsDTO.builder()
                .rideId(rideId)
                .route(RouteDTO.builder()
                        .geometry(List.of(
                                new GeoPoint(45.2671, 19.8335),
                                new GeoPoint(45.2685, 19.8400),
                                new GeoPoint(45.2700, 19.8500)
                        ))
                        .distanceMeters(5100)
                        .durationSeconds(890)
                        .build())
                .cancelled(false)
                .cancelledBy(null)
                .price(820.0)
                .panicTriggered(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
