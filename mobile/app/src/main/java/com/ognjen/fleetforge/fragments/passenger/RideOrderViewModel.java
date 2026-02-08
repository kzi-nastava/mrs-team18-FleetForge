package com.ognjen.fleetforge.fragments.passenger;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.dtos.photon.PhotonResponse;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.repository.RoutingRepo;


import java.io.IOException;
import java.util.List;

public class RideOrderViewModel extends ViewModel {
    private RoutingRepo repo;
    private final MutableLiveData<PhotonResponse> suggestions = new MutableLiveData<>();
    private RoutingService routingService;

    private MutableLiveData<CalculatedRoute> routeData = new MutableLiveData<>();
    public LiveData<CalculatedRoute> getRouteData() { return routeData; }
    public RideOrderViewModel(){
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
