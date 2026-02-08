package com.ognjen.fleetforge.fragments.passenger;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.dtos.photon.PhotonResponse;
import com.ognjen.fleetforge.dtos.ride.RideCreateRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideCreateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.WaypointRideCreateDTO;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.repository.RideRepo;
import com.ognjen.fleetforge.repository.RoutingRepo;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RideOrderViewModel extends ViewModel {
    private RoutingRepo repo;
    private final MutableLiveData<PhotonResponse> suggestions = new MutableLiveData<>();
    private RoutingService routingService;
    private RideRepo rideRepo;

    private MutableLiveData<CalculatedRoute> routeData = new MutableLiveData<>();
    public LiveData<CalculatedRoute> getRouteData() { return routeData; }
    public RideOrderViewModel(){
    }
    public void init(String apiKey) {
        if (repo == null) {
            repo = new RoutingRepo(apiKey);
        }
        routingService= new RoutingService(apiKey);
        rideRepo= new RideRepo();
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

        public LiveData<RideCreateResponseDTO> createRide(ArrayList<WaypointRideCreateDTO> coordinates, int passengerNumber, LocalDateTime rideTime, boolean now
        , ArrayList<String> passengerEmails, VehicleType vehicleType, boolean babySeat, boolean petFriendly, String startAddress, String endAddress
        , double totalDistance, double duration){
            RideCreateRequestDTO requestDTO= new RideCreateRequestDTO();
            requestDTO.setCoordinates(coordinates);
            requestDTO.setPassengerNumber(passengerNumber);
            requestDTO.setRideTime(rideTime.truncatedTo(ChronoUnit.MILLIS));
            requestDTO.setRideNow(now);
            requestDTO.setPassengerEmails(passengerEmails);
            requestDTO.setVehicleType(vehicleType);
            requestDTO.setBabySeat(babySeat);
            requestDTO.setPetFriendly(petFriendly);
            requestDTO.setStartAddress(startAddress);
            requestDTO.setEndAddress(endAddress);
            requestDTO.setTotalDistance(totalDistance);
            requestDTO.setEstimatedDuration(duration);

            return rideRepo.createRide(requestDTO);
        }

}
