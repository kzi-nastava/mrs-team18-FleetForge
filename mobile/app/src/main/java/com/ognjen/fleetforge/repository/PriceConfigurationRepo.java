package com.ognjen.fleetforge.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.price.PriceConfigurationDTO;
import com.ognjen.fleetforge.dtos.price.UpdatePriceConfigurationDTO;
import com.ognjen.fleetforge.enums.VehicleType;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceConfigurationRepo {
    private final RideService service;
    private static final String TAG = "PriceConfigRepo";

    public PriceConfigurationRepo() {
        this.service = RetrofitClient.getInstance().getRideService();
    }

    public LiveData<List<PriceConfigurationDTO>> getAllPriceConfigurations() {
        MutableLiveData<List<PriceConfigurationDTO>> data = new MutableLiveData<>();

        service.getAllPriceConfigurations().enqueue(new Callback<List<PriceConfigurationDTO>>() {
            @Override
            public void onResponse(Call<List<PriceConfigurationDTO>> call, Response<List<PriceConfigurationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    Log.e(TAG, "Error fetching price configurations: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<PriceConfigurationDTO>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch price configurations", t);
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<PriceConfigurationDTO> updatePriceConfiguration(VehicleType vehicleType, UpdatePriceConfigurationDTO dto) {
        MutableLiveData<PriceConfigurationDTO> data = new MutableLiveData<>();

        service.updatePriceConfiguration(vehicleType, dto).enqueue(new Callback<PriceConfigurationDTO>() {
            @Override
            public void onResponse(Call<PriceConfigurationDTO> call, Response<PriceConfigurationDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    try {
                        Log.e(TAG, "Error updating price configuration: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PriceConfigurationDTO> call, Throwable t) {
                Log.e(TAG, "Failed to update price configuration", t);
                data.setValue(null);
            }
        });

        return data;
    }
}

