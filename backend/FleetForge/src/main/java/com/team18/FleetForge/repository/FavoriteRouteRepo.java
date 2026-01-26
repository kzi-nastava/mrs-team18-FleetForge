package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.ride.FavoriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRouteRepo extends JpaRepository<FavoriteRoute, Long> {
    List<FavoriteRoute> findByPassengerId(Long passengerId);
    FavoriteRoute findByRideId(Long rideId);
    Optional<FavoriteRoute> findById(Long id);
}
