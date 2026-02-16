package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.FinishRideResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideStartResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RideService {

    @POST("/api/rides/create")
    Call<RideCreateResponseDTO> createRide(@Body RideCreateRequestDTO request);
    @PUT("/api/rides/{id}/start")
    Call<RideStartResponseDTO> startRide(@Path("id") Long id);

    @PUT("/api/rides/{rideId}/finish")
    Call<FinishRideResponseDTO> finishRide(@Path("rideId") Long rideId);

    @GET("api/rides/active-tracking")
    Call<RideTrackingDTO> getActiveRideTracking();

    @POST("api/rides/driver-location-update")
    Call<DriverLocationUpdateResponseDTO> updateDriverLocation(
            @Body DriverLocationUpdateRequestDTO request
    );
}
