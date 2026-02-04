package com.ognjen.fleetforge.service;

import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DriverApiService {

    @GET("api/rides/active-tracking")
    Call<RideTrackingDTO> getActiveRideTracking();

    @POST("api/rides/driver-location-update")
    Call<DriverLocationUpdateResponseDTO> updateDriverLocation(
            @Body DriverLocationUpdateRequestDTO request
    );
}