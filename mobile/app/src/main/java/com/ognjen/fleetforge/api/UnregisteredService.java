package com.ognjen.fleetforge.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.ognjen.fleetforge.dtos.estimate.RideEstimateRequestDTO;
import com.ognjen.fleetforge.dtos.estimate.RideEstimateResponseDTO;
import com.ognjen.fleetforge.model.VehicleLocation;

public interface UnregisteredService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("api/unregistered-users/active-vehicles")
    Call<List<VehicleLocation>> getActiveVehicles();

    @POST("api/ride-estimates")
    Call<RideEstimateResponseDTO> estimateRide(@Body RideEstimateRequestDTO request);
}