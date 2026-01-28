package com.team18.FleetForge.repository.rides;

import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.ride.RideLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RideLocationRepository extends JpaRepository<RideLocation, Long> {
    List<RideLocation> findByRideOrderByRecordedAtAsc(Ride ride);

    List<RideLocation> findByRideAndRecordedAtAfterOrderByRecordedAtAsc(
            Ride ride,
            LocalDateTime recordedAfter
    );
}

