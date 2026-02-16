package com.ognjen.fleetforge.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.admin.AdminRideDetailsDto;
import com.ognjen.fleetforge.dtos.admin.AdminRideHistoryDto;
import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.repository.AdminRepo;

import java.util.List;

public class AdminHistoryViewModel extends ViewModel {
    private AdminRepo repo = new AdminRepo();
    private int currentPage = 0;
    private final int pageSize = 10;

    public AdminHistoryViewModel(){
        repo= new AdminRepo();
    }

    public LiveData<PageResponse<AdminRideHistoryDto>> getRides(
            String from,
            String to,
            String sortBy,
            String direction,
            String username
    ) {
        return repo.getRides(currentPage, pageSize, from, to, sortBy, direction, username);
    }

    public LiveData<List<String>> searchUsersByPrefix(String prefix) {
        return repo.searchUsersByPrefix(prefix);
    }

    public LiveData<AdminRideDetailsDto> getRideDetails(Long rideId) {
        return repo.getRideDetails(rideId);
    }

    public void resetPage() {
        this.currentPage = 0;
    }

    public void nextPage() { currentPage++; }
    public void prevPage() { if (currentPage > 0) currentPage--; }
    public int getCurrentPage() { return currentPage + 1; }

}
