package com.ognjen.fleetforge.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.ride.FinishRideResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideStartResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideRepo {
    private RideService service;

    public RideRepo(){
        this.service= RetrofitClient.getInstance().getRideService();
    }

    public LiveData<RideCreateResponseDTO> createRide(RideCreateRequestDTO request){
        MutableLiveData<RideCreateResponseDTO> data = new MutableLiveData<>();

        service.createRide(request).enqueue(new Callback<RideCreateResponseDTO>() {
            @Override
            public void onResponse(Call<RideCreateResponseDTO> call, Response<RideCreateResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RideCreateResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<RideStartResponseDTO> startRide(Long id){
        MutableLiveData<RideStartResponseDTO> data= new MutableLiveData<>();

        service.startRide(id).enqueue(new Callback<RideStartResponseDTO>() {
            @Override
            public void onResponse(Call<RideStartResponseDTO> call, Response<RideStartResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<RideStartResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<FinishRideResponseDTO> finishRide(Long id) {
        MutableLiveData<FinishRideResponseDTO> data = new MutableLiveData<>();

        service.finishRide(id).enqueue(new Callback<FinishRideResponseDTO>() {
            @Override
            public void onResponse(Call<FinishRideResponseDTO> call, Response<FinishRideResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<FinishRideResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Error finishing ride: ", throwable);
                data.setValue(null);
            }
        });
        return data;
    }
}
