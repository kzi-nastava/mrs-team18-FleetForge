package com.ognjen.fleetforge.fragments.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.repository.AdminRepo;
import com.ognjen.fleetforge.repository.DriverRepo;
import com.ognjen.fleetforge.repository.PassengerRepo;

public class PasswordChangeViewModel extends ViewModel {
    private DriverRepo driverRepo;
    private AdminRepo adminRepo;
    private PassengerRepo passengerRepo;

    public PasswordChangeViewModel(){
        driverRepo= new DriverRepo();
        adminRepo= new AdminRepo();
        passengerRepo= new PassengerRepo();
    }

    public LiveData<Boolean> changePassword(String newPassword, UserRole role){
        PasswordChangeRequestDTO request= new PasswordChangeRequestDTO();
        request.setNewPassword(newPassword);
        if(role==UserRole.DRIVER){
           return driverRepo.changePassword(request);
        }else if(role== UserRole.ADMIN){
            return adminRepo.changePassword(request);
        }
        else{
           return passengerRepo.changePassword(request);
        }

    }
}
