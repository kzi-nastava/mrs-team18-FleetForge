package com.ognjen.fleetforge.fragments.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.admin.AdminDriverVehicleChangeStatusResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminDriverVehicleInfoChangeDTO;
import com.ognjen.fleetforge.dtos.admin.AdminViewDriverChangesResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminViewVehicleChangesResponseDTO;
import com.ognjen.fleetforge.repository.AdminRepo;

import java.util.ArrayList;

public class DriverChangesViewModel extends ViewModel {

    private LiveData<ArrayList<AdminViewDriverChangesResponseDTO>> driverData;
    private LiveData<ArrayList<AdminViewVehicleChangesResponseDTO>> vehicleData;

    private AdminRepo repo;

    public DriverChangesViewModel(){
        repo= new AdminRepo();
    }

    public LiveData<ArrayList<AdminViewDriverChangesResponseDTO>> getDriversChanges(){
        if(driverData==null){
            driverData= repo.getAllDriversChanges();
        }
        return driverData;
    }

    public LiveData<ArrayList<AdminViewVehicleChangesResponseDTO>> getVehiclesChanges(){
        if(vehicleData==null){
            vehicleData=repo.getAllVehiclesChanges();
        }
        return vehicleData;
    }

    public LiveData<AdminDriverVehicleChangeStatusResponseDTO> driverInfoChange(boolean accepted, long id){
        AdminDriverVehicleInfoChangeDTO req= new AdminDriverVehicleInfoChangeDTO();
        req.setChange(accepted);
       return repo.driverInfoChange(id,req);
    }
    public LiveData<AdminDriverVehicleChangeStatusResponseDTO> vehicleInfoChange(boolean accepted, long id){
        AdminDriverVehicleInfoChangeDTO req= new AdminDriverVehicleInfoChangeDTO();
        req.setChange(accepted);
        return repo.vehicleInfoChange(id,req);
    }
}
