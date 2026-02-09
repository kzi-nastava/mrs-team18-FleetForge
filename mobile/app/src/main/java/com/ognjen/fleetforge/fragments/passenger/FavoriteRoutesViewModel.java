package com.ognjen.fleetforge.fragments.passenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.repository.PassengerRepo;

import java.util.List;

public class FavoriteRoutesViewModel extends ViewModel {
    private PassengerRepo repo;
    private LiveData<List<FavoriteRouteGetResponseDTO>> favRoutes;

    public FavoriteRoutesViewModel(){
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
}
