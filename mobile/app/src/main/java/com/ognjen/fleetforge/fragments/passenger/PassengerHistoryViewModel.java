package com.ognjen.fleetforge.fragments.passenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.repository.PassengerRepo;

import java.util.List;

public class PassengerHistoryViewModel extends ViewModel {
    private PassengerRepo repo;
    private LiveData<List<FavoriteRouteGetResponseDTO>> favRoutes;

    public PassengerHistoryViewModel(){
        repo= new PassengerRepo();
    }


    public LiveData<List<FavoriteRouteGetResponseDTO>> getFavorites() {
        if(favRoutes==null){
            favRoutes= repo.getFavorites();
        }
        return favRoutes;
    }
    public LiveData<Boolean> deleteFavorite(Long id){
        return repo.deleteFavorite(id);
    }

    public LiveData<Boolean> addFavorite(String routeName, Long rideId) {
        return repo.addFavorite(routeName, rideId);
    }
}
