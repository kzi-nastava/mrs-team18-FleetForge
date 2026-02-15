package com.ognjen.fleetforge.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.dtos.estimate.RideEstimateRequestDTO;
import com.ognjen.fleetforge.dtos.estimate.RideEstimateResponseDTO;
import com.ognjen.fleetforge.dtos.photon.PhotonResponse;
import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.WaypointRideCreateDTO;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.repository.RideRepo;
import com.ognjen.fleetforge.repository.RoutingRepo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnregisteredHomeViewModel extends ViewModel {
    private RoutingRepo repo;
    private final MutableLiveData<PhotonResponse> suggestions = new MutableLiveData<>();
    private RoutingService routingService;
    private MutableLiveData<Double> estimatedPrice = new MutableLiveData<>();

    private MutableLiveData<CalculatedRoute> routeData = new MutableLiveData<>();
    public LiveData<CalculatedRoute> getRouteData() { return routeData; }
    public UnregisteredHomeViewModel(){
    }
    public void init(String apiKey) {
        if (repo == null) {
            repo = new RoutingRepo(apiKey);
        }
        routingService= new RoutingService(apiKey);
    }

    public LiveData<PhotonResponse> getSuggestionsData() {
        return suggestions;
    }

    public void fetchSuggestions(String query) {
        repo.getPhotonSuggestions(query).observeForever(response -> {
            suggestions.setValue(response);
        });
    }

    public void fetchPrice(double distanceKm, String vehicleType) {
        RideEstimateRequestDTO request = new RideEstimateRequestDTO(distanceKm, vehicleType);
        RetrofitClient.getInstance().getUnregisteredService().estimateRide(request).enqueue(new Callback<RideEstimateResponseDTO>() {
            @Override
            public void onResponse(Call<RideEstimateResponseDTO> call, Response<RideEstimateResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estimatedPrice.postValue(response.body().getEstimatedPrice());
                }
            }
            @Override
            public void onFailure(Call<RideEstimateResponseDTO> call, Throwable t) { /* log error */ }
        });
    }

    public LiveData<Double> getEstimatedPrice() { return estimatedPrice; }
    public void drawRoute(List<GeoPoint> waypoints) throws IOException {
        new Thread(() -> {
            try {
                CalculatedRoute route = routingService.calculateRoute(waypoints);
                routeData.postValue(route);
            } catch (IOException e) {
                Log.e("ViewModel", "Greška pri ruti: " + e.getMessage());
            }
        }).start();
    }

}
