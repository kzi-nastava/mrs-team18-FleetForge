package com.ognjen.fleetforge.fragments.driver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.ride.RideStartResponseDTO;
import com.ognjen.fleetforge.repository.RideRepo;

public class CurrentRideDriverViewModel extends ViewModel {
    private RideRepo repo;

    public CurrentRideDriverViewModel(){
        repo= new RideRepo();
    }

    public LiveData<RideStartResponseDTO> startRide(Long id){
        return repo.startRide(id);
    }
}
