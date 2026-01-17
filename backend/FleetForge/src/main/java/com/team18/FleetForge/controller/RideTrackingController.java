package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.reports.InconsistencyReportDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideTrackingDTO;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.RideTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideTrackingController {

    private final RideTrackingService rideTrackingService;

    /**
     * GET /api/rides/active-tracking
     * Get active ride tracking for the currently logged-in passenger
     * Returns 404 if no active ride exists
     */
    @GetMapping(
            value = "/active-tracking",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideTrackingDTO> getActiveRideTracking(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        RideTrackingDTO tracking = rideTrackingService.getActiveRideForPassenger(user.getId());

        if (tracking == null) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(tracking, HttpStatus.OK);
    }

    /**
     * POST /api/rides/{rideId}/report-inconsistency
     * Report route inconsistency during an active ride
     * Request Body:
     *  - comment (String, required, max 500 chars)
     *  - currentLocation (GeoPoint, optional)
     * Response:
     *  - reportId (Long)
     *  - message (String)
     *  - reportedAt (LocalDateTime)
     */
    @PostMapping(
            value = "/report-inconsistency",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<InconsistencyReportResponseDTO> reportInconsistency(
            @Valid @RequestBody InconsistencyReportDTO request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        RideTrackingDTO tracking = rideTrackingService.getActiveRideForPassenger(user.getId());

        if (tracking == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new InconsistencyReportResponseDTO(null, "No active ride found", null));
        }

        InconsistencyReportResponseDTO response =
                rideTrackingService.reportInconsistency(tracking.getRideId(), request, user.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}