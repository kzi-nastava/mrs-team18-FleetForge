package com.ognjen.fleetforge.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.AdminService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.admin.ActiveRideDTO;
import com.ognjen.fleetforge.dtos.admin.ActiveRideDetailsDTO;
import com.ognjen.fleetforge.dtos.admin.AdminChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.admin.AdminChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminDriverVehicleChangeStatusResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminDriverVehicleInfoChangeDTO;
import com.ognjen.fleetforge.dtos.admin.AdminGetResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminRideDetailsDto;
import com.ognjen.fleetforge.dtos.admin.AdminRideHistoryDto;
import com.ognjen.fleetforge.dtos.admin.AdminViewDriverChangesResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminViewVehicleChangesResponseDTO;
import com.ognjen.fleetforge.dtos.admin.BlockUserRequestDTO;
import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepo {
    private AdminService service;
    private RideService rideService;

    public AdminRepo(){
        service= RetrofitClient.getInstance().getAdminService();
        rideService = RetrofitClient.getInstance().getRideService();
    }

    public LiveData<ArrayList<AdminViewDriverChangesResponseDTO>> getAllDriversChanges(){
        MutableLiveData<ArrayList<AdminViewDriverChangesResponseDTO>> data= new MutableLiveData<>();

        service.getAllDriversChanges().enqueue(new Callback<ArrayList<AdminViewDriverChangesResponseDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<AdminViewDriverChangesResponseDTO>> call, Response<ArrayList<AdminViewDriverChangesResponseDTO>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AdminViewDriverChangesResponseDTO>> call, Throwable t) {
                    data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<Boolean> handlePanic(Long rideId) {
        MutableLiveData<Boolean> successData = new MutableLiveData<>();

        rideService.handlePanic(rideId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                successData.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                successData.setValue(false);
            }
        });

        return successData;
    }

    public LiveData<ArrayList<AdminViewVehicleChangesResponseDTO>> getAllVehiclesChanges(){
        MutableLiveData<ArrayList<AdminViewVehicleChangesResponseDTO>> data= new MutableLiveData<>();

        service.getAllVehicleChanges().enqueue(new Callback<ArrayList<AdminViewVehicleChangesResponseDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<AdminViewVehicleChangesResponseDTO>> call, Response<ArrayList<AdminViewVehicleChangesResponseDTO>> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AdminViewVehicleChangesResponseDTO>> call, Throwable t) {
                    data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<AdminDriverVehicleChangeStatusResponseDTO> driverInfoChange(Long requestId, AdminDriverVehicleInfoChangeDTO request){
        MutableLiveData<AdminDriverVehicleChangeStatusResponseDTO> data= new MutableLiveData<>();

        service.driverInfoChange(requestId,request).enqueue(new Callback<AdminDriverVehicleChangeStatusResponseDTO>() {
            @Override
            public void onResponse(Call<AdminDriverVehicleChangeStatusResponseDTO> call, Response<AdminDriverVehicleChangeStatusResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<AdminDriverVehicleChangeStatusResponseDTO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;

    }
    public LiveData<AdminDriverVehicleChangeStatusResponseDTO> vehicleInfoChange(Long requestId, AdminDriverVehicleInfoChangeDTO request){
        MutableLiveData<AdminDriverVehicleChangeStatusResponseDTO> data= new MutableLiveData<>();

        service.vehicleInfoChange(requestId,request).enqueue(new Callback<AdminDriverVehicleChangeStatusResponseDTO>() {
            @Override
            public void onResponse(Call<AdminDriverVehicleChangeStatusResponseDTO> call, Response<AdminDriverVehicleChangeStatusResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<AdminDriverVehicleChangeStatusResponseDTO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;

    }
    public LiveData<Boolean> changePassword(PasswordChangeRequestDTO request){
        MutableLiveData<Boolean> data= new MutableLiveData<>();

        service.changePassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    data.setValue(true);
                }else{
                    data.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                data.setValue(true);
            }
        });
        return data;
    }

    public LiveData<AdminGetResponseDTO> getLoggedAdmin(){
        MutableLiveData<AdminGetResponseDTO> data=new MutableLiveData<>();
        service.getLoggedAdmin().enqueue(new Callback<AdminGetResponseDTO>() {
            @Override
            public void onResponse(Call<AdminGetResponseDTO> call, Response<AdminGetResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AdminGetResponseDTO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
    public LiveData<Boolean> uploadProfilePic(Context context, Uri imageUri) throws IOException {
        MutableLiveData<Boolean> result= new MutableLiveData<Boolean>();
        MultipartBody.Part imagePart=null;
        try {
            File file = FileUtil.getFileFromUri(context, imageUri);
            String mimeType = context.getContentResolver().getType(imageUri);
            if (mimeType == null){
                result.setValue(false);
                return result;
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), file);
            imagePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);


            service.uploadProfilePictureCurrentUser(imagePart).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        result.setValue(true);
                    }else{
                        result.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    result.setValue(false);
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
            result.setValue(false);
        }
        return result;
    }
    public LiveData<AdminChangeInformationResponseDTO> changeCurrentAdmin(AdminChangeInformationRequestDTO request){
        MutableLiveData<AdminChangeInformationResponseDTO> data= new MutableLiveData<>();

        service.changeCurrentAdmin(request).enqueue(new Callback<AdminChangeInformationResponseDTO>() {
            @Override
            public void onResponse(Call<AdminChangeInformationResponseDTO> call, Response<AdminChangeInformationResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
                else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<AdminChangeInformationResponseDTO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;

    }

    public LiveData<Boolean> blockUser(Long id,BlockUserRequestDTO requestDTO){
        MutableLiveData<Boolean> data= new MutableLiveData<>();
        service.blockUser(id,requestDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    data.setValue(true);
                }else{
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(false);
            }
        });
        return data;
    }

    public LiveData<PageResponse<AdminRideHistoryDto>> getRides(
            int page,
            int size,
            String from,
            String to,
            String sortBy,
            String direction,
            String username
    ) {
        MutableLiveData<PageResponse<AdminRideHistoryDto>> data = new MutableLiveData<>();

        service.getRides(page, size, sortBy, direction, from, to, username)
                .enqueue(new Callback<PageResponse<AdminRideHistoryDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminRideHistoryDto>> call,
                                           Response<PageResponse<AdminRideHistoryDto>> response) {
                        if (response.isSuccessful()) {
                            data.setValue(response.body());
                        } else {
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminRideHistoryDto>> call, Throwable t) {
                        data.setValue(null);
                    }
                });

        return data;
    }


    public LiveData<AdminRideDetailsDto> getRideDetails(Long rideId) {
        MutableLiveData<AdminRideDetailsDto> data = new MutableLiveData<>();
        service.getRideDetails(rideId).enqueue(new Callback<AdminRideDetailsDto>() {
            @Override
            public void onResponse(Call<AdminRideDetailsDto> call, Response<AdminRideDetailsDto> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AdminRideDetailsDto> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<String>> searchUsersByPrefix(String prefix) {
        MutableLiveData<List<String>> data = new MutableLiveData<>();

        service.searchUsersByPrefix(prefix).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<List<ActiveRideDTO>> getAllActiveRides() {
        MutableLiveData<List<ActiveRideDTO>> data = new MutableLiveData<>();

        rideService.getAllActiveRides().enqueue(new Callback<List<ActiveRideDTO>>() {
            @Override
            public void onResponse(Call<List<ActiveRideDTO>> call, Response<List<ActiveRideDTO>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<ActiveRideDTO>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    public LiveData<ActiveRideDetailsDTO> getActiveRideDetails(Long rideId) {
        MutableLiveData<ActiveRideDetailsDTO> data = new MutableLiveData<>();

        rideService.getActiveRideDetails(rideId).enqueue(new Callback<ActiveRideDetailsDTO>() {
            @Override
            public void onResponse(Call<ActiveRideDetailsDTO> call, Response<ActiveRideDetailsDTO> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ActiveRideDetailsDTO> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }


}
