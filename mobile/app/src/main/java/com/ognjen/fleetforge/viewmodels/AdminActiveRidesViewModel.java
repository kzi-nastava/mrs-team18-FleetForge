package com.ognjen.fleetforge.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.admin.ActiveRideDTO;
import com.ognjen.fleetforge.dtos.admin.ActiveRideDetailsDTO;
import com.ognjen.fleetforge.repository.AdminRepo;

import java.util.List;

public class AdminActiveRidesViewModel extends ViewModel {
    private final AdminRepo repo;

    public AdminActiveRidesViewModel() {
        repo = new AdminRepo();
    }

    public LiveData<List<ActiveRideDTO>> getAllActiveRides() {
        return repo.getAllActiveRides();
    }

    public LiveData<ActiveRideDetailsDTO> getActiveRideDetails(Long rideId) {
        return repo.getActiveRideDetails(rideId);
    }
}

