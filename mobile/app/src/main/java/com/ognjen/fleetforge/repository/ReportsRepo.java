package com.ognjen.fleetforge.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.ReportsService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.reports.UserDataReportResponseDTO;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsRepo {
    private ReportsService service;

    public ReportsRepo(){
        service= RetrofitClient.getInstance().getReportsService();
    }
    public LiveData<UserDataReportResponseDTO> getDataForLoggedUser(LocalDate fromDate, LocalDate toDate){
        MutableLiveData<UserDataReportResponseDTO> data= new MutableLiveData<>();
        service.getDataForLoggedUser(fromDate,toDate).enqueue(new Callback<UserDataReportResponseDTO>() {
            @Override
            public void onResponse(Call<UserDataReportResponseDTO> call, Response<UserDataReportResponseDTO> response) {
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
            public void onFailure(Call<UserDataReportResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });



        return data;
    }

    public LiveData<UserDataReportResponseDTO> getDataForUser(LocalDate fromDate, LocalDate toDate, String email){
        MutableLiveData<UserDataReportResponseDTO> data= new MutableLiveData<>();
        service.getDataForUser(fromDate,toDate,email).enqueue(new Callback<UserDataReportResponseDTO>() {
            @Override
            public void onResponse(Call<UserDataReportResponseDTO> call, Response<UserDataReportResponseDTO> response) {
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
            public void onFailure(Call<UserDataReportResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });


        return data;
    }

    public LiveData<UserDataReportResponseDTO> getDataForReport(LocalDate fromDate, LocalDate toDate){
        MutableLiveData<UserDataReportResponseDTO> data= new MutableLiveData<>();
        service.getDataForReport(fromDate,toDate).enqueue(new Callback<UserDataReportResponseDTO>() {
            @Override
            public void onResponse(Call<UserDataReportResponseDTO> call, Response<UserDataReportResponseDTO> response) {
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
            public void onFailure(Call<UserDataReportResponseDTO> call, Throwable throwable) {
                android.util.Log.e("API_FAILURE", "Doslo je do greske: ", throwable);
                data.setValue(null);
            }
        });

        return data;
    }
}
