package com.ognjen.fleetforge.fragments.passenger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.PassengerGetResponseDTO;
import com.ognjen.fleetforge.repository.PassengerRepo;

public class PassengerProfileViewModel extends ViewModel {
    private LiveData<PassengerGetResponseDTO> profileData;
    private PassengerRepo repo;

    public PassengerProfileViewModel(){
        repo=new PassengerRepo();
    }

    public LiveData<PassengerGetResponseDTO> getPassengerProfile(){
        if(profileData==null){
            profileData=repo.getLoggedPassenger();
        }
        return profileData;
    }

}
