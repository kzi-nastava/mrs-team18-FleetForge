package com.ognjen.fleetforge.api;

import com.ognjen.fleetforge.model.mapbox.MapboxDirectionsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapboxApiService {

    /**
     * Get driving directions between waypoints
     *
     * @param coordinates Semicolon-separated list of {longitude},{latitude} coordinates
     * @param geometries Format of the geometry (geojson, polyline, polyline6)
     * @param overview Level of detail (full, simplified, false)
     * @param accessToken Your Mapbox API access token
     * @return Response containing route geometry and metadata
     *
     * Example: /directions/v5/mapbox/driving/-73.989,40.733;-74.005,40.717
     */
    @GET("directions/v5/mapbox/driving/{coordinates}")
    Call<MapboxDirectionsResponse> getDirections(
            @Path(value = "coordinates", encoded = true) String coordinates,
            @Query("geometries") String geometries,
            @Query("overview") String overview,
            @Query("access_token") String accessToken
    );
}