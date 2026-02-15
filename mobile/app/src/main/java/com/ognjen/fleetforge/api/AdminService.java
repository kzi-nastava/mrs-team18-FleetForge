package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.admin.AdminChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.admin.AdminChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminDriverVehicleChangeStatusResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminDriverVehicleInfoChangeDTO;
import com.ognjen.fleetforge.dtos.admin.AdminGetResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminViewDriverChangesResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminViewVehicleChangesResponseDTO;
import com.ognjen.fleetforge.dtos.admin.BlockUserRequestDTO;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerGetResponseDTO;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AdminService {
    @GET("/api/admin/profile-change-requests")
    Call<ArrayList<AdminViewDriverChangesResponseDTO>> getAllDriversChanges();

    @GET("/api/admin/vehicle-change-requests")
    Call<ArrayList<AdminViewVehicleChangesResponseDTO>> getAllVehicleChanges();

    @PUT("/api/admin/{requestId}/driver-info")
    Call<AdminDriverVehicleChangeStatusResponseDTO> driverInfoChange(@Path("requestId") Long requestId, @Body AdminDriverVehicleInfoChangeDTO request);

    @PUT("/api/admin/{requestId}/vehicle-info")
    Call<AdminDriverVehicleChangeStatusResponseDTO> vehicleInfoChange(@Path("requestId")Long requestId, @Body AdminDriverVehicleInfoChangeDTO request);
    @PUT("/api/drivers/password")
    Call<Void> changePassword(@Body PasswordChangeRequestDTO request);

    @GET("/api/admin")
    Call<AdminGetResponseDTO> getLoggedAdmin();
    @Multipart
    @POST("api/users/upload-profile-picture")
    Call<Void> uploadProfilePictureCurrentUser(@Part MultipartBody.Part file);

    @PUT("/api/admin")
    Call<AdminChangeInformationResponseDTO> changeCurrentAdmin(@Body AdminChangeInformationRequestDTO request);

    @PUT("/api/admin/block/{id}")
    Call<Void> blockUser(@Path("id")Long id, @Body BlockUserRequestDTO requestDTO);
}
