package com.ognjen.fleetforge.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.ride.ScheduledRideDto;
import com.ognjen.fleetforge.repository.RideRepo;

public class ScheduledRidesViewModel extends ViewModel {

    private RideRepo repo = new RideRepo();
    private int currentPage = 0;
    private final int pageSize = 10;

    private MutableLiveData<Boolean> cancelResult = new MutableLiveData<>();

    public LiveData<PageResponse<ScheduledRideDto>> getScheduledRides() {
        return repo.getScheduledRides(currentPage, pageSize);
    }

    public LiveData<Boolean> getCancelResult() {
        return cancelResult;
    }

    public void cancelRide(Long rideId) {
        repo.cancelRide(rideId, null).observeForever(success -> {
            cancelResult.setValue(success);
        });
    }

    public void nextPage() { currentPage++; }

    public void prevPage() {
        if (currentPage > 0) currentPage--;
    }

    public void resetPage() { currentPage = 0; }
}
