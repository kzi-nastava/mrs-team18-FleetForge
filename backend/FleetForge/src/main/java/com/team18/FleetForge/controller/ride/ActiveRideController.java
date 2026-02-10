package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.dto.ride.view.ActiveRideDTO;
import com.team18.FleetForge.dto.ride.view.ActiveRideDetailsDTO;
import com.team18.FleetForge.service.rides.ActiveRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ActiveRideController {

    private final ActiveRideService activeRideService;

    /**
     * GET /api/rides/active
     *
     * Fetches basic information for all currently active (IN_PROGRESS) rides.
     * This endpoint is called ONCE when the admin opens the active rides page.
     * Returns lightweight data for displaying in a list view.
     * @return List of ActiveRideDTOs with basic ride information
     */
    @GetMapping("/active")
    public ResponseEntity<List<ActiveRideDTO>> getAllActiveRides() {
        List<ActiveRideDTO> activeRides = activeRideService.getAllActiveRides();
        return ResponseEntity.ok(activeRides);
    }

    /**
     * GET /api/rides/active/{rideId}
     *
     *
     * @param rideId ID of the ride to monitor
     * @return ActiveRideDetailsDTO with live data, or 404 if ride not found/not active
     */
    @GetMapping("/active/{rideId}")
    public ResponseEntity<ActiveRideDetailsDTO> getActiveRideLiveData(@PathVariable Long rideId) {
        ActiveRideDetailsDTO liveData = activeRideService.getActiveRideLiveData(rideId);

        if (liveData == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(liveData);
    }
}