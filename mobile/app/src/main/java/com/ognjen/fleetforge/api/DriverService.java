package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.driver.CompletedRideDTO;
import com.ognjen.fleetforge.dtos.driver.DriverCreateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverCreateResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverGetResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeResponseDTO;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeRequestDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

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

    @POST("/api/drivers")
    Call<DriverCreateResponseDTO> createDriver(@Body DriverCreateRequestDTO request);
    @Multipart
    @POST("/api/users/upload-profile-picture/{id}")
    Call<Boolean> uploadProfilePictureById(@Path("id") Long id,@Part MultipartBody.Part file);
    @GET("api/rides/active-tracking")
    Call<RideTrackingDTO> getActiveRideTracking();

    @POST("api/rides/driver-location-update")
    Call<DriverLocationUpdateResponseDTO> updateDriverLocation(
            @Body DriverLocationUpdateRequestDTO request
    );

    @GET("/api/drivers/ride-history")
    Call<List<CompletedRideDTO>> getDriverRideHistory();
}
