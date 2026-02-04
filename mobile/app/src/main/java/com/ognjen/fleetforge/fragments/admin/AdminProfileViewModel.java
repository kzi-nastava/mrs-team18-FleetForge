package com.ognjen.fleetforge.fragments.admin;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.admin.AdminChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.admin.AdminChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminGetResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.repository.AdminRepo;

import java.io.IOException;

public class AdminProfileViewModel extends ViewModel {
    private LiveData<AdminGetResponseDTO> profileData;
    private AdminRepo repo;
    private final MutableLiveData<Uri> selectedImageUri = new MutableLiveData<>();

    public void setSelectedImageUri(Uri uri) {
        selectedImageUri.setValue(uri);
    }

    public LiveData<Uri> getSelectedImageUri() {
        return selectedImageUri;
    }

    public AdminProfileViewModel(){
        repo= new AdminRepo();
    }
    public LiveData<AdminGetResponseDTO> getAdminProfile(){
        if(profileData==null){
            profileData=repo.getLoggedAdmin();
        }
        return profileData;
    }
    public LiveData<Boolean> uploadProfilePicture(Context context, Uri imageUri) throws IOException {
        return repo.uploadProfilePic(context,imageUri);
    }
    public LiveData<AdminChangeInformationResponseDTO> changeCurrentAdmin(String firstName
            , String lastName, String email, String phoneNumber, String address){
        AdminChangeInformationRequestDTO request= new AdminChangeInformationRequestDTO();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setAddress(address);
        request.setPhoneNumber(phoneNumber);
        request.setEmail(email);
        return repo.changeCurrentAdmin(request);
    }
}
