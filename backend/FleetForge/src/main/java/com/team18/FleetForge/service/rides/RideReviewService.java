package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.ride.review.RideReviewRequestDTO;
import com.team18.FleetForge.dto.ride.review.RideReviewResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.RideReview;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.users.PassengerRepository;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.rides.RideReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class RideReviewService {

    private final RideRepository rideRepository;
    private final RideReviewRepository rideReviewRepository;
    private final PassengerRepository passengerRepository;

    private static final long REVIEW_DEADLINE_DAYS = 3;

    @Transactional
    public RideReviewResponseDTO createReview(Long rideId, Long passengerId, RideReviewRequestDTO request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new RuntimeException("Cannot review ride. Ride must be completed. Current status: " + ride.getStatus());
        }

        if (ride.getEndTime() == null) {
            throw new RuntimeException("Cannot review ride. Ride has no end time recorded.");
        }

        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + passengerId));

        if (!ride.getPassenger().getId().equals(passengerId)) {
            throw new RuntimeException("You are not authorized to review this ride. Only the main passenger can leave a review.");
        }

        if (rideReviewRepository.existsByRideId(rideId)) {
            throw new RuntimeException("Review already exists for this ride. Cannot submit multiple reviews.");
        }

        long daysSinceRideEnded = ChronoUnit.DAYS.between(ride.getEndTime(), LocalDateTime.now());
        if (daysSinceRideEnded > REVIEW_DEADLINE_DAYS) {

             throw new RuntimeException("Review deadline has passed. Reviews must be submitted within 3 days of ride completion.");
        }

        RideReview review = RideReview.builder()
                .ride(ride)
                .passenger(passenger)
                .vehicleRating(request.getVehicleRating())
                .driverRating(request.getDriverRating())
                .comment(request.getComment())
                .reviewedAt(LocalDateTime.now())
                .build();

        review = rideReviewRepository.save(review);

        return RideReviewResponseDTO.builder()
                .rideId(ride.getId())
                .vehicleRating(review.getVehicleRating())
                .driverRating(review.getDriverRating())
                .comment(review.getComment())
                .reviewedAt(review.getReviewedAt())
                .build();
    }

    @Transactional
    public RideReviewResponseDTO getReview(Long rideId) {
        RideReview review = rideReviewRepository.findByRideId(rideId)
                .orElseThrow(() -> new RuntimeException("Review not found for ride id: " + rideId));

        return RideReviewResponseDTO.builder()
                .rideId(review.getRide().getId())
                .vehicleRating(review.getVehicleRating())
                .driverRating(review.getDriverRating())
                .comment(review.getComment())
                .reviewedAt(review.getReviewedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean canReviewRide(Long rideId, Long passengerId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.COMPLETED || ride.getEndTime() == null) {
            return false;
        }

        if (!ride.getPassenger().getId().equals(passengerId)) {
            return false;
        }

        if (rideReviewRepository.existsByRideId(rideId)) {
            return false;
        }

        return true;
    }
}