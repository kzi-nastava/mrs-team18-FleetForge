package com.ognjen.fleetforge.utils;

import com.ognjen.fleetforge.service.DriverApiService;
import com.ognjen.fleetforge.service.UnregisteredApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static RetrofitClient instance;
    private Retrofit retrofit;

    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public UnregisteredApiService getUnregisteredApiService() {
        return retrofit.create(UnregisteredApiService.class);
    }

//    public PassengerApiService getPassengerApiService() {
//        return retrofit.create(PassengerApiService.class);
//    }
//
//    public AdminApiService getAdminApiService() {
//        return retrofit.create(AdminApiService.class);
//    }
//
    public DriverApiService getDriverApiService() {
        return retrofit.create(DriverApiService.class);
    }
}