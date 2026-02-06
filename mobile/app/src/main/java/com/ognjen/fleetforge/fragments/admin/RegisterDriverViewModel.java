package com.ognjen.fleetforge.fragments.admin;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.driver.DriverCreateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverCreateResponseDTO;
import com.ognjen.fleetforge.dtos.vehicle.VehicleCreateRequestDTO;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.repository.DriverRepo;

import java.io.IOException;

public class RegisterDriverViewModel extends ViewModel {
    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();
    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
    }

    public LiveData<Uri> getSelectedImageUri() {
        return selectedImageUri;
    }
    private DriverRepo repo;

    public RegisterDriverViewModel(){
        repo=new DriverRepo();
    }


    public LiveData<DriverCreateResponseDTO> createDriver(String firstName, String lastName
    , String email, String phoneNumber, String address, String model, VehicleType type, String registrationNumber
    , int space, boolean babySeat, boolean petFriendly) throws IOException {
        VehicleCreateRequestDTO vehicleReq= new VehicleCreateRequestDTO();
        vehicleReq.setModel(model);
        vehicleReq.setType(type);
        vehicleReq.setBabySeat(babySeat);
        vehicleReq.setSpace(space);
        vehicleReq.setPetFriendly(petFriendly);
        vehicleReq.setRegistrationNumber(registrationNumber);
        DriverCreateRequestDTO driverReq= new DriverCreateRequestDTO();
        driverReq.setVehicle(vehicleReq);
        driverReq.setFirstName(firstName);
        driverReq.setLastName(lastName);
        driverReq.setAddress(address);
        driverReq.setEmail(email);
        driverReq.setPhoneNumber(phoneNumber);

        return repo.createDriver(driverReq);

    }

    public LiveData<Boolean> uploadProfilePictureById(Context context, Uri imageUri, Long driverId) throws IOException {
        return repo.uploadProfilePicById(context,imageUri,driverId);
    }
}
