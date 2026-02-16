package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.reports.UserDataReportResponseDTO;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportsService {

    @GET("/api/reports/logged-user/report-data")
    Call<UserDataReportResponseDTO> getDataForLoggedUser(@Query("fromDate") LocalDate fromDate,@Query("toDate") LocalDate toDate);

    @GET("/api/reports/admin-by-user/report-data")
    Call<UserDataReportResponseDTO> getDataForUser(@Query("fromDate") LocalDate fromDate,@Query("toDate") LocalDate toDate,
                                                   @Query("email") String email);
    @GET("/api/reports/admin/report-data")
    Call<UserDataReportResponseDTO> getDataForReport(@Query("fromDate") LocalDate fromDate,@Query("toDate") LocalDate toDate);
}
