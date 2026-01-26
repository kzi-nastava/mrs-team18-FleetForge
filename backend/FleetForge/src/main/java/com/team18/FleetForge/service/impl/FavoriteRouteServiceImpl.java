package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.ride.FavoriteRoute;
import com.team18.FleetForge.repository.FavoriteRouteRepo;
import com.team18.FleetForge.service.FavoriteRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteRouteServiceImpl implements FavoriteRouteService {
    private final FavoriteRouteRepo favoriteRouteRepo;

    @Override
    public List<FavoriteRoute> findByPassengerId(Long passengerId) {
        return favoriteRouteRepo.findByPassengerId(passengerId);
    }

    @Override
    public FavoriteRoute findById(Long rideId) {
        return  favoriteRouteRepo.findById(rideId).orElse(null);
    }

    @Override
    public void save(FavoriteRoute route) {
        favoriteRouteRepo.save(route);
    }

    @Override
    public void delete(FavoriteRoute route) {
        favoriteRouteRepo.delete(route);
    }
}
