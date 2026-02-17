package com.ognjen.fleetforge.fragments.driver;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.ride.FinishRideResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideStartResponseDTO;
import com.ognjen.fleetforge.repository.RideRepo;
import com.ognjen.fleetforge.utils.SingleLiveEvent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRideDriverViewModel extends ViewModel {

    private static final String TAG = "CurrentRideDriverViewModel";
    private final RideRepo repo;

    private final MutableLiveData<RideStartResponseDTO> startRideLiveData = new MutableLiveData<>();
    private final SingleLiveEvent<FinishRideResponseDTO> finishRideLiveData = new SingleLiveEvent<>();

    public CurrentRideDriverViewModel(){
        this.repo= new RideRepo();
    }

    public LiveData<FinishRideResponseDTO> getFinishRideObservable() {
        return finishRideLiveData;
    }

    public LiveData<RideStartResponseDTO> startRide(Long id){
        return repo.startRide(id);
    }

    public void finishRide(Long rideId) {
        repo.finishRide(rideId).observeForever(result -> {
            finishRideLiveData.setValue(result);
        });
    }
}
