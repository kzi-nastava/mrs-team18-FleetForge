package com.team18.FleetForge.controller.ride;

import com.team18.FleetForge.dto.ride.review.RideReviewRequestDTO;
import com.team18.FleetForge.dto.ride.review.RideReviewResponseDTO;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.RideReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideReviewController {

    private final RideReviewService rideReviewService;

    /**
     * Create a review for a completed ride
     * POST /api/rides/{rideId}/review
     * request:
     * driverRating: Integer (1-5)
     * vehicleRating: Integer (1-5)
     * comment: String (optional)
     * response:
     * rideId: Long
     * driverRating: Integer
     * vehicleRating: Integer
     * comment: String
     * createdAt: LocalDateTime
     * Passenger ID is extracted from JWT token (@AuthenticationPrincipal)
     */
    @PostMapping("/{rideId}/review")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<?> createReview(
            @PathVariable Long rideId,
            @Valid @RequestBody RideReviewRequestDTO request,
            @AuthenticationPrincipal User authenticatedUser) {

        try {
            Long passengerId = authenticatedUser.getId();
            RideReviewResponseDTO response = rideReviewService.createReview(rideId, passengerId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get review for a specific ride
     * GET /api/rides/{rideId}/review
     */
    @GetMapping("/{rideId}/review")
    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<?> getReview(@PathVariable Long rideId) {

        try {
            RideReviewResponseDTO response = rideReviewService.getReview(rideId);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Check if a passenger can review a ride
     * GET /api/rides/{rideId}/can-review
     *
     * Useful for frontend to show/hide review button
     */
    @GetMapping("/{rideId}/can-review")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Map<String, Boolean>> canReview(
            @PathVariable Long rideId,
            @AuthenticationPrincipal User authenticatedUser) {

        try {
            Long passengerId = authenticatedUser.getId();
            boolean canReview = rideReviewService.canReviewRide(rideId, passengerId);

            return ResponseEntity.ok(Map.of("canReview", canReview));

        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("canReview", false));
        }
    }
}