package com.ognjen.fleetforge.fragments.reports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ognjen.fleetforge.dtos.reports.UserDataReportResponseDTO;
import com.ognjen.fleetforge.repository.ReportsRepo;

import java.time.LocalDate;

public class UserReportsViewModel extends ViewModel {

    private ReportsRepo repo;


    public UserReportsViewModel(){
        repo= new ReportsRepo();
    }

    public LiveData<UserDataReportResponseDTO> getUsersData(LocalDate fromDate,LocalDate toDate)
    {
        return repo.getDataForLoggedUser(fromDate, toDate);
    }
    public LiveData<UserDataReportResponseDTO> getAdminAllUsersData(LocalDate fromDate,LocalDate toDate){
        return repo.getDataForReport(fromDate,toDate);
    }

    public LiveData<UserDataReportResponseDTO> getAdminUserData(LocalDate fromDate,LocalDate toDate,String email){
       return repo.getDataForUser(fromDate,toDate,email);
    }





}
