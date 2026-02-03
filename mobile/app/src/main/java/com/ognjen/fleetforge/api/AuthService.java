package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.LoginRequestDTO;
import com.ognjen.fleetforge.dtos.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);
}