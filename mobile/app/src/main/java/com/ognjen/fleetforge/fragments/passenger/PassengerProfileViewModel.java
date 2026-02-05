package com.ognjen.fleetforge.fragments.passenger;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerGetResponseDTO;
import com.ognjen.fleetforge.repository.PassengerRepo;

import java.io.IOException;

public class PassengerProfileViewModel extends ViewModel {
    private LiveData<PassengerGetResponseDTO> profileData;
    private PassengerRepo repo;
    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();

    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
    }

    public LiveData<Uri> getSelectedImageUri() {
        return selectedImageUri;
    }
    public PassengerProfileViewModel(){
        repo=new PassengerRepo();
    }

    public LiveData<PassengerGetResponseDTO> getPassengerProfile(){
        if(profileData==null){
            profileData=repo.getLoggedPassenger();
        }
        return profileData;
    }
    public LiveData<Boolean> uploadProfilePicture(Context context, Uri imageUri) throws IOException {
        return repo.uploadProfilePic(context,imageUri);
    }

    public LiveData<PassengerChangeInformationResponseDTO> changeCurrentPassenger(String firstName
    ,String lastName, String email, String phoneNumber, String address){
        PassengerChangeInformationRequestDTO request= new PassengerChangeInformationRequestDTO();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setAddress(address);
        request.setPhoneNumber(phoneNumber);
        request.setEmail(email);
        return repo.changeCurrentPassenger(request);
    }
}
