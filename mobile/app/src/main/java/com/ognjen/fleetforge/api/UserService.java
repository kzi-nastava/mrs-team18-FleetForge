package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.admin.GetAllUsersDTO;
import com.ognjen.fleetforge.dtos.user.GetIsBlockedUserDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserService {

    @GET("/api/users")
    Call<GetAllUsersDTO> getAllUsers(@Query("page") int page, @Query("size") int size, @Query("email") String email);
    @GET("/api/users/blocked")
    Call<GetIsBlockedUserDTO> checkIfBlocked();
}
