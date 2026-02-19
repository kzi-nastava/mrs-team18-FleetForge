package com.ognjen.fleetforge.fragments.passenger;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRidePassengerViewModel extends ViewModel {

    private static final String TAG = "CurrentRidePassengerVM";
    private final RideService rideService;
    private final MutableLiveData<RideTrackingDTO> rideTrackingData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> noActiveRide = new MutableLiveData<>();

    public CurrentRidePassengerViewModel() {
        this.rideService = RetrofitClient.getInstance().getRideService();
    }

    public LiveData<RideTrackingDTO> getRideTrackingData() {
        return rideTrackingData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getNoActiveRide() {
        return noActiveRide;
    }

    public void fetchActiveRideTracking() {
        Call<RideTrackingDTO> call = rideService.getActiveRideTracking();
        call.enqueue(new Callback<RideTrackingDTO>() {
            @Override
            public void onResponse(Call<RideTrackingDTO> call, Response<RideTrackingDTO> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        rideTrackingData.postValue(response.body());
                        Log.d(TAG, "Active ride tracking fetched: " + response.body().getRideId());
                    } else {
                        noActiveRide.postValue(true);
                        Log.d(TAG, "No active ride found");
                    }
                } else {
                    errorMessage.postValue("Failed to load ride data: " + response.code());
                    Log.e(TAG, "Error fetching ride: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RideTrackingDTO> call, Throwable t) {
                errorMessage.postValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error", t);
            }
        });
    }
}