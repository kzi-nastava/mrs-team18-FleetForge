package com.ognjen.fleetforge.fragments.driver;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.ride.CancellationRequest;
import com.ognjen.fleetforge.dtos.ride.FinishRideResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RidePanicResponseDTO;
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
    private final MediatorLiveData<FinishRideResponseDTO> finishRideLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> cancelRideResult = new MediatorLiveData<>();
    private final MediatorLiveData<RidePanicResponseDTO> panicLiveData = new MediatorLiveData<>();

    public CurrentRideDriverViewModel(){
        this.repo= new RideRepo();
    }

    public LiveData<FinishRideResponseDTO> getFinishRideObservable() {
        return finishRideLiveData;
    }
    public LiveData<Boolean> getCancelRideObservable() { return cancelRideResult; }
    public LiveData<RidePanicResponseDTO> getPanicObservable() { return panicLiveData; }

    public LiveData<RideStartResponseDTO> startRide(Long id) { return repo.startRide(id); }

    public void cancelRide(Long rideId, String reason) {
        LiveData<Boolean> source = repo.cancelRide(rideId, reason);
        cancelRideResult.addSource(source, success -> {
            cancelRideResult.setValue(success);
            cancelRideResult.removeSource(source);
        });
    }

    public void finishRide(Long rideId) {
        LiveData<FinishRideResponseDTO> source = repo.finishRide(rideId);
        finishRideLiveData.addSource(source, result -> {
            finishRideLiveData.setValue(result);
            finishRideLiveData.removeSource(source);
        });
    }

    public void triggerPanic(Long rideId) {
        LiveData<RidePanicResponseDTO> source = repo.triggerPanic(rideId);
        panicLiveData.addSource(source, response -> {
            panicLiveData.setValue(response);
            panicLiveData.removeSource(source);
        });
    }

}