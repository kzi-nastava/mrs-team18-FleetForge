package com.ognjen.fleetforge.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.auth.AuthInterceptor;
import com.ognjen.fleetforge.auth.AuthManager;

public class RetrofitClient {
    private static final String BaseUrl = "http://" + BuildConfig.IP_ADDR + ":8080";
    private static RetrofitClient client;
    private Retrofit retrofit;
    private Retrofit retrofitWithoutAuth;

    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        AuthInterceptor authInterceptor = new AuthInterceptor(AuthManager.getInstance());

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Logic for Public APIs (without JWT)
        OkHttpClient okHttpClientWithoutAuth = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofitWithoutAuth = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClientWithoutAuth)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (client == null) {
            client = new RetrofitClient();
        }
        return client;
    }

    public PassengerService getPassengerService() {
        return retrofit.create(PassengerService.class);
    }
    public DriverService getDriverService(){
        return retrofit.create(DriverService.class);
    }
    public AdminService getAdminService(){
        return retrofit.create(AdminService.class);
    }
}

    public AuthService getLoginService() {
        return retrofitWithoutAuth.create(AuthService.class);
    }
}
