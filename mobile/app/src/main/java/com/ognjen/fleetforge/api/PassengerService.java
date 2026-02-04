package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerGetResponseDTO;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface PassengerService {

    @GET("/api/passenger")
    Call<PassengerGetResponseDTO> getLoggedPassenger();
    @Multipart
    @POST("api/users/upload-profile-picture")
    Call<Void> uploadProfilePictureCurrentUser(@Part MultipartBody.Part file);

    @PUT("/api/passenger/password")
    Call<Void> changePassword(@Body PasswordChangeRequestDTO request);
    @PUT("/api/passenger")
    Call<PassengerChangeInformationResponseDTO> changeCurrentPassenger(@Body PassengerChangeInformationRequestDTO request);
}
