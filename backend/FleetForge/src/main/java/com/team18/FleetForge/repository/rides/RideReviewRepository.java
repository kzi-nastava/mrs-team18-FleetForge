package com.team18.FleetForge.repository.rides;

import com.team18.FleetForge.model.ride.RideReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideReviewRepository extends JpaRepository<RideReview, Long> {

    boolean existsByRideId(Long rideId);

    Optional<RideReview> findByRideId(Long rideId);

    Optional<RideReview> findByRideIdAndPassengerId(Long rideId, Long passengerId);
}