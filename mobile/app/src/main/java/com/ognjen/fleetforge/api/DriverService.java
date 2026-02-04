package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.driver.DriverGetResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeResponseDTO;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeRequestDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeResponseDTO;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface DriverService {

    @GET("/api/drivers")
    Call<DriverGetResponseDTO> getLoggedDriver();

    @POST("/api/drivers/update-request")
    Call<DriverProfileChangeResponseDTO> createDriverChangeRequest(@Body DriverProfileChangeRequestDTO request);

    @POST("/api/drivers/update-request-vehicle")
    Call<VehicleInformationChangeResponseDTO> createVehicleChangeRequest(@Body VehicleInformationChangeRequestDTO request);

    @Multipart
    @POST("api/users/upload-profile-picture")
    Call<Void> uploadProfilePictureCurrentUser(@Part MultipartBody.Part file);

    @PUT("/api/drivers/password")
    Call<Void> changePassword(@Body PasswordChangeRequestDTO request);
}
