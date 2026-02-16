package com.ognjen.fleetforge.repository;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.PassengerService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerGetResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideDetailsDto;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;
import com.ognjen.fleetforge.model.FavoriteRoute;
import com.ognjen.fleetforge.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public LiveData<PassengerRideDetailsDto> getRideDetails(Long rideId) {
        MutableLiveData<PassengerRideDetailsDto> data = new MutableLiveData<>();
        service.getRideDetails(rideId).enqueue(new Callback<PassengerRideDetailsDto>() {
            @Override
            public void onResponse(Call<PassengerRideDetailsDto> call, Response<PassengerRideDetailsDto> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PassengerRideDetailsDto> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
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
        public LiveData<List<FavoriteRouteGetResponseDTO>> getFavorites(){
            MutableLiveData<List<FavoriteRouteGetResponseDTO>> data= new MutableLiveData<>();

            service.getFavorites().enqueue(new Callback<List<FavoriteRouteGetResponseDTO>>() {
                @Override
                public void onResponse(Call<List<FavoriteRouteGetResponseDTO>> call, Response<List<FavoriteRouteGetResponseDTO>> response) {
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
                public void onFailure(Call<List<FavoriteRouteGetResponseDTO>> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
                }
            });
            return data;
        }

        public LiveData<Boolean> deleteFavorite(Long id){
            MutableLiveData<Boolean> data= new MutableLiveData<>();

            service.deleteFavorite(id).enqueue(new Callback<Void>() {
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

        public LiveData<Boolean> addFavorite(String routeName, Long rideId){
            MutableLiveData<Boolean> data= new MutableLiveData<>();
            service.addFavorite(routeName,rideId).enqueue(new Callback<Void>() {
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

    public LiveData<PageResponse<PassengerRideHistoryDto>> getRides(int page, int size, String from, String to, String sortBy, String direction) {
        MutableLiveData<PageResponse<PassengerRideHistoryDto>> data = new MutableLiveData<>();
        service.getPassengerRides(page, size, sortBy, direction, from, to).enqueue(new Callback<PageResponse<PassengerRideHistoryDto>>() {
            @Override
            public void onResponse(Call<PageResponse<PassengerRideHistoryDto>> call, Response<PageResponse<PassengerRideHistoryDto>> response) {
                if (response.isSuccessful()) data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<PageResponse<PassengerRideHistoryDto>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
