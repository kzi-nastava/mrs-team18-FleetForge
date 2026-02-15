package com.ognjen.fleetforge.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.UserService;
import com.ognjen.fleetforge.dtos.admin.GetAllUsersDTO;
import com.ognjen.fleetforge.dtos.user.GetIsBlockedUserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersRepo {
    private UserService service;

    public UsersRepo(){
        service= RetrofitClient.getInstance().getUserService();
    }

    public LiveData<GetAllUsersDTO> getAllUsers(int page, int size, String email){
        MutableLiveData<GetAllUsersDTO> data= new MutableLiveData<>();

        service.getAllUsers(page,size,email).enqueue(new Callback<GetAllUsersDTO>() {
            @Override
            public void onResponse(Call<GetAllUsersDTO> call, Response<GetAllUsersDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GetAllUsersDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });

        return data;
    }
    public LiveData< GetIsBlockedUserDTO> checkIfBlocked(){
        MutableLiveData<GetIsBlockedUserDTO> data= new MutableLiveData<>();
        service.checkIfBlocked().enqueue(new Callback<GetIsBlockedUserDTO>() {
            @Override
            public void onResponse(Call<GetIsBlockedUserDTO> call, Response<GetIsBlockedUserDTO> response) {
                if(response.isSuccessful()){
                    data.setValue(response.body());
                }else{
                    try {
                        android.util.Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<GetIsBlockedUserDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });
        return data;
    }
}
