package com.ognjen.fleetforge.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.AdminService;
import com.ognjen.fleetforge.api.RetrofitClient;
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
import com.ognjen.fleetforge.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepo {
    private AdminService service;

    public AdminRepo(){
        service= RetrofitClient.getInstance().getAdminService();
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
}
