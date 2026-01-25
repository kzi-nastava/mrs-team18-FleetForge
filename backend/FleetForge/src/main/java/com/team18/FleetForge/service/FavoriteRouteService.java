package com.team18.FleetForge.service;

import com.team18.FleetForge.model.ride.FavoriteRoute;

import java.util.List;

public interface FavoriteRouteService {
    List<FavoriteRoute> findByPassengerId(Long passengerId);
    FavoriteRoute findById(Long rideId);
    void save(FavoriteRoute route);
    void  delete(FavoriteRoute route);
}
