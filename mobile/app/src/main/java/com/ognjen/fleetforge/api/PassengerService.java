package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerChangeInformationResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerGetResponseDTO;
import com.ognjen.fleetforge.dtos.common.PasswordChangeRequestDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideDetailsDto;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PassengerService {

    @GET("/api/passenger")
    Call<PassengerGetResponseDTO> getLoggedPassenger();
    @Multipart
    @POST("api/users/upload-profile-picture")
    Call<Void> uploadProfilePictureCurrentUser(@Part MultipartBody.Part file);

    @PUT("/api/passenger/password")
    Call<Void> changePassword(@Body PasswordChangeRequestDTO request);
    @PUT("/api/passenger")
    Call<PassengerChangeInformationResponseDTO> changeCurrentPassenger(@Body PassengerChangeInformationRequestDTO request);

    @GET("/api/passenger/favorites")
    Call<List<FavoriteRouteGetResponseDTO>> getFavorites();
    @DELETE("/api/passenger/favorites/{id}")
    Call<Void> deleteFavorite(@Path("id") Long id);

    @POST("/api/passenger/favorites/{routeName}/{rideId}")
    Call<Void> addFavorite(@Path("routeName")String routeName, @Path("rideId")Long rideId);

    @GET("/api/passenger/rides")
    Call<PageResponse<PassengerRideHistoryDto>> getPassengerRides(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("from") String from,
            @Query("to") String to
    );

    @GET("/api/passenger/rides/{id}")
    Call<PassengerRideDetailsDto> getRideDetails(@Path("id") Long id);
}
