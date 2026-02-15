package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.dtos.photon.PhotonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PhotonApiService {
        @GET("api/")
        Call<PhotonResponse> getSuggestions(
                @Query("q") String query,
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("lang") String lang,
                @Query("limit") int limit,
                @Query("bbox") String bbox
        );

}
