package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideService {

    @POST("/api/rides/create")
    Call<RideCreateResponseDTO> createRide(@Body RideCreateRequestDTO request);
}
