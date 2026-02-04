package com.ognjen.fleetforge.service;

import android.util.Log;

import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.model.mapbox.MapboxDirectionsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoutingService {
    private static final String TAG = "RoutingService";
    private static final String MAPBOX_BASE_URL = "https://api.mapbox.com/";

    private final MapboxApiService apiService;
    private final String accessToken;

    public RoutingService(String accessToken) {
        this.accessToken = accessToken;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MAPBOX_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(MapboxApiService.class);
    }

    /**
     * Calculate route between multiple waypoints
     *
     * @param waypoints List of points to navigate through (in order)
     * @return CalculatedRoute with coordinates, distance, and time
     * @throws IOException if the API call fails
     */
    public CalculatedRoute calculateRoute(List<GeoPoint> waypoints) throws IOException {
        if (waypoints == null || waypoints.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 waypoints for routing");
        }

        StringBuilder coordinates = new StringBuilder();
        for (int i = 0; i < waypoints.size(); i++) {
            GeoPoint point = waypoints.get(i);
            coordinates.append(point.getLongitude()).append(",").append(point.getLatitude());
            if (i < waypoints.size() - 1) {
                coordinates.append(";");
            }
        }

        Log.d(TAG, "Calculating route with coordinates: " + coordinates);

        Call<MapboxDirectionsResponse> call = apiService.getDirections(
                coordinates.toString(),
                "geojson",
                "full",
                accessToken
        );

        Response<MapboxDirectionsResponse> response = call.execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Mapbox API error: " + response.code() + " " + response.message());
        }

        MapboxDirectionsResponse directionsResponse = response.body();

        if (directionsResponse.getRoutes() == null || directionsResponse.getRoutes().isEmpty()) {
            throw new IOException("No routes found");
        }

        MapboxDirectionsResponse.Route route = directionsResponse.getRoutes().get(0);

        List<GeoPoint> routeCoordinates = new ArrayList<>();
        for (List<Double> coord : route.getGeometry().getCoordinates()) {
            double longitude = coord.get(0);
            double latitude = coord.get(1);
            routeCoordinates.add(new GeoPoint(latitude, longitude));
        }

        double distanceKm = route.getDistanceKm();
        int estimatedMinutes = route.getDurationMinutes();

        Log.d(TAG, String.format("Route calculated: %.2f km, %d min, %d coordinates",
                distanceKm, estimatedMinutes, routeCoordinates.size()));

        return new CalculatedRoute(routeCoordinates, distanceKm, estimatedMinutes);
    }

    public CalculatedRoute calculateRoute(GeoPoint start, GeoPoint end) throws IOException {
        List<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(start);
        waypoints.add(end);
        return calculateRoute(waypoints);
    }
}