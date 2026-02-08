package com.ognjen.fleetforge.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ognjen.fleetforge.api.MapboxApiService;
import com.ognjen.fleetforge.api.PhotonApiService;
import com.ognjen.fleetforge.dtos.photon.PhotonResponse;
import com.ognjen.fleetforge.model.mapbox.MapboxDirectionsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoutingRepo {
    private static final String MAPBOX_BASE_URL = "https://api.mapbox.com/";
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/";
    private final MapboxApiService apiService;
    private final String accessToken;
    private static final String PHOTON_BASE_URL = "https://photon.komoot.io/";
    private final PhotonApiService photonApiService;
    public RoutingRepo(String accessToken) {
        this.accessToken = accessToken;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MAPBOX_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(MapboxApiService.class);



        Retrofit photonRetrofit = new Retrofit.Builder()
                .baseUrl(PHOTON_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.photonApiService = photonRetrofit.create(PhotonApiService.class);
    }

    public LiveData<PhotonResponse> getPhotonSuggestions(String query) {
        MutableLiveData<PhotonResponse> data = new MutableLiveData<>();

        photonApiService.getSuggestions(query, 45.2671, 19.8335, "en", 10)
                .enqueue(new Callback<PhotonResponse>() {
                    @Override
                    public void onResponse(Call<PhotonResponse> call, Response<PhotonResponse> response) {
                        if (response.isSuccessful()) data.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<PhotonResponse> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }


}
