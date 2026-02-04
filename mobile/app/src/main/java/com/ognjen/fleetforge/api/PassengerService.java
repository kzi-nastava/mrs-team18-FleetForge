package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.PassengerGetResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PassengerService {

    @GET("/api/passenger")
    Call<PassengerGetResponseDTO> getLoggedPassenger();

}
