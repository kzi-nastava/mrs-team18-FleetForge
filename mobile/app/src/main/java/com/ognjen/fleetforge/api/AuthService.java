package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.LoginRequestDTO;
import com.ognjen.fleetforge.dtos.LoginResponseDTO;
import com.ognjen.fleetforge.dtos.PasswordResetRequestDTO;
import com.ognjen.fleetforge.dtos.ResetPasswordRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);

    @POST("/api/auth/password-reset-requests")
    Call<Void> requestPasswordReset(@Body PasswordResetRequestDTO data);

    @POST("/api/auth/password-resets")
    Call<Void> resetPassword(@Body ResetPasswordRequestDTO request);
}