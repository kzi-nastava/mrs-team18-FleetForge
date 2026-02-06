package com.ognjen.fleetforge.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.DriverService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverCreateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverCreateResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverGetResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeResponseDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeRequestDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeResponseDTO;
import com.ognjen.fleetforge.utils.FileUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRepo {
    private DriverService service;

    public DriverRepo(){
        service= RetrofitClient.getInstance().getDriverService();
    }

    public LiveData<DriverGetResponseDTO> getLoggedDriver(){
        MutableLiveData<DriverGetResponseDTO> data= new MutableLiveData<>();

        service.getLoggedDriver().enqueue(new Callback<DriverGetResponseDTO>() {
            @Override
            public void onResponse(Call<DriverGetResponseDTO> call, Response<DriverGetResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DriverGetResponseDTO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<DriverProfileChangeResponseDTO> createDriverChangeRequest(DriverProfileChangeRequestDTO request){
        MutableLiveData<DriverProfileChangeResponseDTO> data= new MutableLiveData<>();

        service.createDriverChangeRequest(request).enqueue(new Callback<DriverProfileChangeResponseDTO>() {
            @Override
            public void onResponse(Call<DriverProfileChangeResponseDTO> call, Response<DriverProfileChangeResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DriverProfileChangeResponseDTO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<VehicleInformationChangeResponseDTO> createVehicleChangeRequest(VehicleInformationChangeRequestDTO request){
        MutableLiveData<VehicleInformationChangeResponseDTO> data= new MutableLiveData<>();

        service.createVehicleChangeRequest(request).enqueue(new Callback<VehicleInformationChangeResponseDTO>() {
            @Override
            public void onResponse(Call<VehicleInformationChangeResponseDTO> call, Response<VehicleInformationChangeResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<VehicleInformationChangeResponseDTO> call, Throwable t) {
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
                        try {
                            android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                        } catch (Exception e) { e.printStackTrace(); }
                        result.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    android.util.Log.e("API_FAILURE", "Došlo je do greške: ", t);
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

    public LiveData<DriverCreateResponseDTO> createDriver(DriverCreateRequestDTO request){
        MutableLiveData<DriverCreateResponseDTO> data= new MutableLiveData<>();

        service.createDriver(request).enqueue(new Callback<DriverCreateResponseDTO>() {
            @Override
            public void onResponse(Call<DriverCreateResponseDTO> call, Response<DriverCreateResponseDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }
                else{
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DriverCreateResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Došlo je do greške: ", throwable);
                data.setValue(null);
            }
        });
        return data;
    }


    public LiveData<Boolean> uploadProfilePicById(Context context, Uri imageUri,Long driverId) throws IOException {
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


            service.uploadProfilePictureById(driverId,imagePart).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.isSuccessful()){
                        result.setValue(true);
                    }else{
                        try {
                            android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                        } catch (Exception e) { e.printStackTrace(); }
                        result.setValue(false);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                    android.util.Log.e("API_FAILURE", "Doslo je do greske: ", t);
                    result.setValue(false);
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
            result.setValue(null);
        }
        return result;
    }
}
