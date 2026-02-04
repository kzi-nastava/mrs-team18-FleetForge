package com.ognjen.fleetforge.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.PassengerService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerGetResponseDTO;
import com.ognjen.fleetforge.utils.FileUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRepo {
    private PassengerService service;

    public PassengerRepo(){
        this.service= RetrofitClient.getInstance().getPassengerService();
    }

    public LiveData<PassengerGetResponseDTO> getLoggedPassenger(){
            MutableLiveData<PassengerGetResponseDTO> data=new MutableLiveData<>();
            service.getLoggedPassenger().enqueue(new Callback<PassengerGetResponseDTO>() {
                @Override
                public void onResponse(Call<PassengerGetResponseDTO> call, Response<PassengerGetResponseDTO> response) {
                    if(response.isSuccessful()){
                        data.setValue(response.body());
                    }
                }

                @Override
                public void onFailure(Call<PassengerGetResponseDTO> call, Throwable t) {
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

        public LiveData<PassengerChangeInformationResponseDTO> changeCurrentPassenger(PassengerChangeInformationRequestDTO request){
                MutableLiveData<PassengerChangeInformationResponseDTO> data= new MutableLiveData<>();

                service.changeCurrentPassenger(request).enqueue(new Callback<PassengerChangeInformationResponseDTO>() {
                    @Override
                    public void onResponse(Call<PassengerChangeInformationResponseDTO> call, Response<PassengerChangeInformationResponseDTO> response) {
                        if(response.isSuccessful()){
                            data.setValue(response.body());
                        }
                        else{
                            data.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<PassengerChangeInformationResponseDTO> call, Throwable t) {
                            data.setValue(null);
                    }
                });
                return data;

        }
}
