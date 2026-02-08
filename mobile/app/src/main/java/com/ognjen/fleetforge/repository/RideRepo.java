package com.ognjen.fleetforge.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;

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
}
