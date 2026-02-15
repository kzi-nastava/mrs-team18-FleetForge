package com.ognjen.fleetforge.fragments.driver;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.driver.DriverGetResponseDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverProfileChangeResponseDTO;
import com.ognjen.fleetforge.dtos.user.GetIsBlockedUserDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeRequestDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleInformationChangeResponseDTO;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.repository.DriverRepo;
import com.ognjen.fleetforge.repository.UsersRepo;

import java.io.IOException;

public class DriverProfileViewModel extends ViewModel {

    private DriverRepo repo;
    private LiveData<DriverGetResponseDTO> data;
    private UsersRepo usersRepo;
    private LiveData<GetIsBlockedUserDTO> blocked;

    public LiveData<GetIsBlockedUserDTO> checkBlocked() {
        if(blocked==null){
            blocked=usersRepo.checkIfBlocked();
        }
        return blocked;
    }
    public DriverProfileViewModel(){
        repo= new DriverRepo();
        usersRepo= new UsersRepo();
    }
    public LiveData<DriverGetResponseDTO> getLoggedDriver(){
        if(data==null){
            data= repo.getLoggedDriver();
        }
        return data;
    }
    public LiveData<DriverProfileChangeResponseDTO> createDriverChangeRequest(String newFirstName
    ,String newLastName, String newEmail, String newPhoneNumber, String newAddress){
        DriverProfileChangeRequestDTO request= new DriverProfileChangeRequestDTO();
        request.setNewFirstName(newFirstName);
        request.setNewLastName(newLastName);
        request.setNewEmail(newEmail);
        request.setNewAddress(newAddress);
        request.setNewPhoneNumber(newPhoneNumber);

       return repo.createDriverChangeRequest(request);
    }

    public LiveData<VehicleInformationChangeResponseDTO> createVehicleChangeRequest(String model
    , VehicleType type, String regNumber, int passengers, boolean petFriendly, boolean babySeat){
        VehicleInformationChangeRequestDTO request= new VehicleInformationChangeRequestDTO();
        request.setNewModel(model);
        request.setNewType(type);
        request.setNewSpace(passengers);
        request.setNewBabySeat(babySeat);
        request.setNewPetFriendly(petFriendly);
        request.setNewRegistrationNumber(regNumber);

        return repo.createVehicleChangeRequest(request);
    }
    public LiveData<Boolean> uploadProfilePicture(Context context, Uri imageUri) throws IOException {
        return repo.uploadProfilePic(context,imageUri);
    }
}
