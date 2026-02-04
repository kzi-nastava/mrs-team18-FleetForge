package com.ognjen.fleetforge.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import com.ognjen.fleetforge.model.VehicleLocation;

public interface UnregisteredApiService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("api/unregistered-users/active-vehicles")
    Call<List<VehicleLocation>> getActiveVehicles();
}