package com.ognjen.fleetforge.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.PassengerService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.PassengerGetResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRepo {
    private PassengerService service;

    public PassengerRepo(){
        this.service= RetrofitClient.getInstance().getPassengerService();
    }

    public LiveData<PassengerGetResponseDTO> getLoggedPassenger(){
            MutableLiveData<PassengerGetResponseDTO> data=new MutableLiveData<>();
            service.getLoggedPassenger().enqueue(new Callback<PassengerGetResponseDTO>() {
                @Override
                public void onResponse(Call<PassengerGetResponseDTO> call, Response<PassengerGetResponseDTO> response) {
                    if(response.isSuccessful()){
                        data.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(Call<PassengerGetResponseDTO> call, Throwable t) {
                    data.setValue(null);
                }
            });
            return data;
    }

}
