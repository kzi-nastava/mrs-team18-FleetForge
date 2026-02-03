package com.ognjen.fleetforge.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.ognjen.fleetforge.BuildConfig;

public class RetrofitClient {
    private static final String BaseUrl = "http://" + BuildConfig.IP_ADDR + ":8080";
    private static RetrofitClient client;
    private Retrofit retrofit;
    private Retrofit retrofitWithoutAuth;

    private RetrofitClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = new Interceptor() {
            @NonNull
            @Override
            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                Request originalRequest = chain.request();

                String token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJGbGVldEZvcmdlQXBwIiwic3ViIjoicGFzc2VuZ2VyMUB0ZXN0LmNvbSIsImlhdCI6MTc3MDE0NzgyOSwiZXhwIjoxNzcwMTQ5NjI5LCJyb2xlcyI6IlJPTEVfUEFTU0VOR0VSIn0.i8lTbih-v7UobUbWFcJy5i7fHNnsrqxkakwSUFIQWMk1bJg9sFojF1Kr1qRwOK0jylV-od8nEhdCf1zXapaI_g";

                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();

                return chain.proceed(newRequest);
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Client without interceptor
        OkHttpClient okHttpClientWithoutAuth = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
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

    public AuthService getLoginService() {
        return retrofitWithoutAuth.create(AuthService.class);
    }
}