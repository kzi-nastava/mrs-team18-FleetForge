package com.ognjen.fleetforge.fragments.driver;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.ride.CancellationRequest;
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
    private final SingleLiveEvent<Boolean> cancelRideResult = new SingleLiveEvent<>();

    public CurrentRideDriverViewModel(){
        this.repo= new RideRepo();
    }

    public LiveData<FinishRideResponseDTO> getFinishRideObservable() {
        return finishRideLiveData;
    }

    public LiveData<Boolean> getCancelRideObservable() { return cancelRideResult; }

    public LiveData<RideStartResponseDTO> startRide(Long id) { return repo.startRide(id); }

    public void cancelRide(Long rideId, String reason) {
        repo.cancelRide(rideId, reason).observeForever(success -> {
            cancelRideResult.setValue(success);
        });
    }

    public void finishRide(Long rideId) {
        repo.finishRide(rideId).observeForever(result -> {
            finishRideLiveData.setValue(result);
        });
    }

}