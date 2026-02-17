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
    private final MutableLiveData<Boolean> cancelRideResult = new MutableLiveData<>();

    public CurrentRideDriverViewModel(){
        this.repo= new RideRepo();
    }

    public LiveData<FinishRideResponseDTO> getFinishRideObservable() {
        return finishRideLiveData;
    }

    public LiveData<RideStartResponseDTO> startRide(Long id){
        return repo.startRide(id);
    }

    public LiveData<Boolean> cancelRide(Long rideId, String reason) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        rideService.cancelRide(rideId, new CancellationRequest(reason)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }

    public LiveData<FinishRideResponseDTO> finishRide(Long rideId) {
        Call<FinishRideResponseDTO> call = rideService.finishRide(rideId);
        call.enqueue(new Callback<FinishRideResponseDTO>() {
            @Override
            public void onResponse(Call<FinishRideResponseDTO> call, Response<FinishRideResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    finishRideLiveData.postValue(response.body());
                    Log.d(TAG, "Ride finished successfully: " + response.body().getRideId());
                } else {
                    Log.e(TAG, "Failed to finish ride: " + response.code());
                    finishRideLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<FinishRideResponseDTO> call, Throwable t) {
                Log.e(TAG, "Error finishing ride", t);
                finishRideLiveData.postValue(null);
            }
    public void finishRide(Long rideId) {
        repo.finishRide(rideId).observeForever(result -> {
            finishRideLiveData.setValue(result);
        });
    }
}
