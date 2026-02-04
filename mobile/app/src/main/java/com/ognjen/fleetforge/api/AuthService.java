package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.LoginRequestDTO;
import com.ognjen.fleetforge.dtos.LoginResponseDTO;
import com.ognjen.fleetforge.dtos.PasswordResetRequestDTO;
import com.ognjen.fleetforge.dtos.ResetPasswordRequestDTO;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AuthService {

    @POST("/api/auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO loginRequest);

    @POST("/api/auth/password-reset-requests")
    Call<Void> requestPasswordReset(@Body PasswordResetRequestDTO data);

    @POST("/api/auth/password-resets")
    Call<Void> resetPassword(@Body ResetPasswordRequestDTO request);

    @Multipart
    @POST("api/auth/register")
    Call<Void> registerUser(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("phoneNumber") RequestBody phone,
            @Part("address") RequestBody address
    );
}