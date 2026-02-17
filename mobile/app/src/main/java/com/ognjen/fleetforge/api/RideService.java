package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.CancellationRequest;
import com.ognjen.fleetforge.dtos.ride.FinishRideResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideStartResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;
import com.ognjen.fleetforge.dtos.ride.ScheduledRideDto;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("/api/rides/scheduled")
    Call<PageResponse<ScheduledRideDto>> getScheduledRides(
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("/api/rides/{rideId}/cancellations")
    Call<Void> cancelRide(
            @Path("rideId") Long rideId,
            @Body CancellationRequest body
    );
}
