package com.ognjen.fleetforge.fragments.passenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideDetailsDto;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;
import com.ognjen.fleetforge.repository.PassengerRepo;

import java.util.List;

public class PassengerHistoryViewModel extends ViewModel {
    private PassengerRepo repo = new PassengerRepo();
    private int currentPage = 0;
    private final int pageSize = 10;
    private LiveData<List<FavoriteRouteGetResponseDTO>> favRoutes;
    private MutableLiveData<PageResponse<PassengerRideHistoryDto>> ridesLiveData
            = new MutableLiveData<>();

    public PassengerHistoryViewModel(){
        repo= new PassengerRepo();
    }

    public LiveData<PageResponse<PassengerRideHistoryDto>> getRides() {
        return ridesLiveData;
    }

    public void fetchRides(String from, String to, String sortBy, String direction) {
        repo.getRides(currentPage, pageSize, from, to, sortBy, direction)
                .observeForever(ridesLiveData::setValue);
    }

    public LiveData<PassengerRideDetailsDto> getRideDetails(Long rideId) {
        return repo.getRideDetails(rideId);
    }

    public void resetPage() {
        this.currentPage = 0;
    }

    public void nextPage() { currentPage++; }
    public void prevPage() { if (currentPage > 0) currentPage--; }
    public int getCurrentPage() { return currentPage + 1; }

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
