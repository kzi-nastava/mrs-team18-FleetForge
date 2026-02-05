package com.ognjen.fleetforge.fragments.driver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;
import com.ognjen.fleetforge.dtos.ride.WaypointDTO;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.api.DriverService;
import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.utils.MapManager;
import com.ognjen.fleetforge.utils.RouteSimulator;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRideDriver extends Fragment {

    private static final String TAG = "CurrentRideDriver";
    private static final String MAPBOX_API_KEY = BuildConfig.MAPBOX_API_KEY;

    private MapView mapView;
    private FrameLayout loadingOverlay;
    private TextView noRideMessage;

    private ImageView passengerProfileImage;
    private TextView passengerName;
    private TextView passengerPhone;

    private TextView departureAddress;
    private TextView destinationAddress;
    private TextView distanceText;
    private TextView timeText;

    private Button btnPrimaryAction;
    private Button btnSecondaryAction;

    private MapManager mapManager;
    private RoutingService routingService;
    private DriverService driverApiService;

    private RideTrackingDTO currentRide;
    private CalculatedRoute calculatedRoute;
    private View infoCard;

    private RouteSimulator routeSimulator;
    private Handler simulationHandler;
    private Runnable simulationRunnable;
    private static final int SIMULATION_INTERVAL_MS = 2000; // 2 seconds
    private static final int SIMULATION_STEP_SIZE = 5; // Skip 5 coordinates per update
    private static final double WAYPOINT_THRESHOLD = 0.0005; // ~50 meters

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_ride_driver_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeMap();
        initializeServices();

        fetchActiveRide();
    }

    private void initializeViews(View view) {
        Log.d(TAG, "Step 1: Initializing views");

        mapView = view.findViewById(R.id.map_view);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        noRideMessage = view.findViewById(R.id.no_ride_message);

        passengerProfileImage = view.findViewById(R.id.passenger_profile_image);
        passengerName = view.findViewById(R.id.passenger_name);
        passengerPhone = view.findViewById(R.id.passenger_phone);

        departureAddress = view.findViewById(R.id.departure_address);
        destinationAddress = view.findViewById(R.id.destination_address);
        distanceText = view.findViewById(R.id.distance_text);
        timeText = view.findViewById(R.id.time_text);

        btnPrimaryAction = view.findViewById(R.id.btn_primary_action);
        btnSecondaryAction = view.findViewById(R.id.btn_secondary_action);

        btnPrimaryAction.setOnClickListener(v -> onPrimaryActionClick());
        btnSecondaryAction.setOnClickListener(v -> onSecondaryActionClick());

        infoCard = view.findViewById(R.id.info_card);
    }

    private void initializeMap() {
        Log.d(TAG, "Step 1: Initializing map");

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        mapManager = new MapManager(mapView, requireContext());
        mapManager.centerOnDefault();
    }

    private void initializeServices() {
        Log.d(TAG, "Step 1: Initializing services");
        driverApiService = RetrofitClient.getInstance().getDriverService();
        routingService = new RoutingService(MAPBOX_API_KEY);
        simulationHandler = new Handler(Looper.getMainLooper());
    }

    private void fetchActiveRide() {
        Log.d(TAG, "Step 2: Fetching active ride");

        Call<RideTrackingDTO> call = driverApiService.getActiveRideTracking();
        call.enqueue(new Callback<RideTrackingDTO>() {
            @Override
            public void onResponse(Call<RideTrackingDTO> call, Response<RideTrackingDTO> response) {

                if (response.isSuccessful() && response.body() != null) {
                    currentRide = response.body();
                    Log.d(TAG, "Active ride found: " + currentRide.getRideId());

                    displayRideData();
                    calculateAndDisplayRoute();

                } else if (response.code() == 404) {
                    showNoRideMessage();
                } else {
                    showError("Failed to load ride data");
                }
            }

            @Override
            public void onFailure(Call<RideTrackingDTO> call, Throwable t) {
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayRideData() {
        if (currentRide == null) return;

        displayPassengerInfo();
        displayRouteInfo();
        configureButtons();
    }

    private void displayPassengerInfo() {
        if (currentRide.getPassenger() == null) return;

        String fullName = currentRide.getPassenger().getFirstName() + " " +
                currentRide.getPassenger().getLastName();
        passengerName.setText(fullName);
        passengerPhone.setText(currentRide.getPassenger().getPhoneNumber());

        Log.d(TAG, "Passenger: " + fullName);
    }

    private void displayRouteInfo() {
        if (currentRide.getRoute() == null) return;

        departureAddress.setText(currentRide.getRoute().getStartAddress());
        destinationAddress.setText(currentRide.getRoute().getEndAddress());

        Log.d(TAG, "Route: " + currentRide.getRoute().getStartAddress() +
                " → " + currentRide.getRoute().getEndAddress());
    }

    private void configureButtons() {
        String status = currentRide.getStatus();

        if ("ACCEPTED".equals(status)) {
            btnPrimaryAction.setText("Start Ride");
            btnPrimaryAction.setBackgroundTintList(
                    getResources().getColorStateList(R.color.primary_orange, null));

            btnSecondaryAction.setText("Cancel");
            btnSecondaryAction.setBackgroundTintList(
                    getResources().getColorStateList(R.color.error, null));

            Log.d(TAG, "Buttons configured for ACCEPTED status");

        } else if ("IN_PROGRESS".equals(status)) {
            btnPrimaryAction.setText("Finish Ride");
            btnPrimaryAction.setBackgroundTintList(
                    getResources().getColorStateList(R.color.success, null));

            btnSecondaryAction.setText("SOS");
            btnSecondaryAction.setBackgroundTintList(
                    getResources().getColorStateList(R.color.panic, null));

            Log.d(TAG, "Buttons configured for IN_PROGRESS status");
        }
    }

    private void calculateAndDisplayRoute() {
        Log.d(TAG, "Step 4: Starting route calculation");

        new Thread(() -> {
            try {
                List<GeoPoint> waypoints = buildWaypointList();

                calculatedRoute = routingService.calculateRoute(waypoints);

                Log.d(TAG, String.format("Route calculated: %.2f km, %d min, %d coordinates",
                        calculatedRoute.getDistanceKm(),
                        calculatedRoute.getEstimatedMinutes(),
                        calculatedRoute.getCoordinates().size()));

                new Handler(Looper.getMainLooper()).post(() -> {
                    displayRouteOnMap();
                    updateDistanceAndTime();

                    initializeAndStartSimulation();
                });

            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    showError("Failed to calculate route: " + e.getMessage());
                });
            }
        }).start();
    }

    private List<GeoPoint> buildWaypointList() {
        List<GeoPoint> waypoints = new ArrayList<>();

        if (currentRide == null || currentRide.getRoute() == null) {
            return waypoints;
        }

        String status = currentRide.getStatus();
        GeoPoint currentLocation = currentRide.getCurrentLocation();

        if ("ACCEPTED".equals(status)) {
            waypoints.add(currentLocation);
            waypoints.add(currentRide.getRoute().getStartLocation());

        } else if ("IN_PROGRESS".equals(status)) {
            waypoints.add(currentLocation);

            if (currentRide.getRoute().getWaypoints() != null) {
                for (WaypointDTO wp : currentRide.getRoute().getWaypoints()) {
                    if (!wp.getIsCompleted()) {
                        waypoints.add(wp.getLocation());
                    }
                }
            }

            waypoints.add(currentRide.getRoute().getEndLocation());
        }

        return waypoints;
    }

    private void displayRouteOnMap() {
        Log.d(TAG, "Step 5: Displaying route on map");

        if (calculatedRoute == null) return;
        mapManager.clearAll();

        List<org.osmdroid.util.GeoPoint> osmPoints = new ArrayList<>();
        for (GeoPoint point : calculatedRoute.getCoordinates()) {
            osmPoints.add(new org.osmdroid.util.GeoPoint(
                    point.getLatitude(),
                    point.getLongitude()
            ));
        }

        mapManager.drawRoute(osmPoints, 0xFFFF9900); // Orange route

        addRouteMarkers();

        if (!osmPoints.isEmpty()) {
            mapView.getController().setCenter(osmPoints.get(0));
            mapView.getController().setZoom(14.0);
        }

        Log.d(TAG, "Route displayed with " + osmPoints.size() + " points");
    }

    private void addRouteMarkers() {
        if (currentRide == null || currentRide.getRoute() == null) return;

        String status = currentRide.getStatus();

        if ("ACCEPTED".equals(status)) {
            GeoPoint start = currentRide.getRoute().getStartLocation();
            mapManager.addMarker(
                    start.getLatitude(),
                    start.getLongitude(),
                    "Pickup Location",
                    R.drawable.ic_map_point
            );

        } else if ("IN_PROGRESS".equals(status)) {
            GeoPoint end = currentRide.getRoute().getEndLocation();
            mapManager.addMarker(
                    end.getLatitude(),
                    end.getLongitude(),
                    "Destination",
                    R.drawable.ic_map_point
            );

            if (currentRide.getRoute().getWaypoints() != null) {
                for (WaypointDTO wp : currentRide.getRoute().getWaypoints()) {
                    if (!wp.getIsCompleted()) {
                        mapManager.addMarker(
                                wp.getLocation().getLatitude(),
                                wp.getLocation().getLongitude(),
                                "Stop " + wp.getOrder(),
                                R.drawable.ic_map_point
                        );
                    }
                }
            }
        }

        GeoPoint current = currentRide.getCurrentLocation();
        mapManager.addMarker(
                current.getLatitude(),
                current.getLongitude(),
                "Your Location",
                R.drawable.ic_current_taxi
        );

        Log.d(TAG, "Markers added to map");
    }

    private void updateDistanceAndTime() {
        Log.d(TAG, "Step 6: Updating distance and time");

        if (calculatedRoute == null) return;

        String distance = String.format(Locale.getDefault(), "%.1f km",
                calculatedRoute.getDistanceKm());
        distanceText.setText(distance);

        String time = calculatedRoute.getEstimatedMinutes() + " min";
        timeText.setText(time);

        Log.d(TAG, "UI updated: " + distance + ", " + time);
    }

    private void initializeAndStartSimulation() {
        if (calculatedRoute == null || calculatedRoute.getCoordinates().isEmpty()) {
            Log.e(TAG, "Cannot start simulation: no route coordinates");
            return;
        }
        routeSimulator = new RouteSimulator(
                calculatedRoute.getCoordinates(),
                SIMULATION_STEP_SIZE
        );
        startSimulation();
    }

    private void startSimulation() {
        Log.d(TAG, "Step 8: Starting live simulation");

        simulationRunnable = new Runnable() {
            @Override
            public void run() {
                if (routeSimulator == null || routeSimulator.isComplete()) {
                    Log.d(TAG, "Simulation complete - arrived at destination");
                    onSimulationComplete();
                    return;
                }

                GeoPoint nextPoint = routeSimulator.getNextCoordinate();

                if (nextPoint != null) {
                    currentRide.setCurrentLocation(nextPoint);
                    updateDriverMarkerOnMap(nextPoint);
                    sendLocationUpdateToBackend(nextPoint);
                    checkWaypointCompletion(nextPoint);
                    updateRemainingDistanceAndTime();

                    simulationHandler.postDelayed(this, SIMULATION_INTERVAL_MS);
                }
            }
        };

        simulationHandler.post(simulationRunnable);
    }

    private void updateDriverMarkerOnMap(GeoPoint newLocation) {
        mapManager.clearAll();

        List<org.osmdroid.util.GeoPoint> osmPoints = new ArrayList<>();
        for (GeoPoint point : calculatedRoute.getCoordinates()) {
            osmPoints.add(new org.osmdroid.util.GeoPoint(
                    point.getLatitude(),
                    point.getLongitude()
            ));
        }
        mapManager.drawRoute(osmPoints, 0xFFFF9900);

        String status = currentRide.getStatus();

        if ("ACCEPTED".equals(status)) {
            GeoPoint start = currentRide.getRoute().getStartLocation();
            mapManager.addMarker(
                    start.getLatitude(),
                    start.getLongitude(),
                    "Pickup Location",
                    R.drawable.ic_map_point
            );
        } else if ("IN_PROGRESS".equals(status)) {
            GeoPoint end = currentRide.getRoute().getEndLocation();
            mapManager.addMarker(
                    end.getLatitude(),
                    end.getLongitude(),
                    "Destination",
                    R.drawable.ic_map_point
            );

            if (currentRide.getRoute().getWaypoints() != null) {
                for (WaypointDTO wp : currentRide.getRoute().getWaypoints()) {
                    if (!wp.getIsCompleted()) {
                        mapManager.addMarker(
                                wp.getLocation().getLatitude(),
                                wp.getLocation().getLongitude(),
                                "Stop " + wp.getOrder(),
                                R.drawable.ic_map_point
                        );
                    }
                }
            }
        }

        mapManager.addMarker(
                newLocation.getLatitude(),
                newLocation.getLongitude(),
                "Your Location",
                R.drawable.ic_current_taxi
        );
    }

    private void updateRemainingDistanceAndTime() {
        String status = currentRide.getStatus();
        List<GeoPoint> remainingRoute = new ArrayList<>();
        remainingRoute.add(routeSimulator.getCurrentCoordinate());

        if ("ACCEPTED".equals(status)) {
            remainingRoute.add(currentRide.getRoute().getStartLocation());

        } else if ("IN_PROGRESS".equals(status)) {
            if (currentRide.getRoute().getWaypoints() != null) {
                for (WaypointDTO wp : currentRide.getRoute().getWaypoints()) {
                    if (!wp.getIsCompleted()) {
                        remainingRoute.add(wp.getLocation());
                    }
                }
            }
            remainingRoute.add(currentRide.getRoute().getEndLocation());
        }

        new Thread(() -> {
            try {
                CalculatedRoute remaining = routingService.calculateRoute(remainingRoute);

                new Handler(Looper.getMainLooper()).post(() -> {
                    distanceText.setText(String.format(Locale.getDefault(), "%.1f km", remaining.getDistanceKm()));
                    timeText.setText(remaining.getEstimatedMinutes() + " min");
                });
            } catch (IOException e) {
                Log.e(TAG, "Failed to recalculate route", e);
            }
        }).start();
    }

    private void sendLocationUpdateToBackend(GeoPoint location) {
        DriverLocationUpdateRequestDTO request = new DriverLocationUpdateRequestDTO(location);

        Call<DriverLocationUpdateResponseDTO> call = driverApiService.updateDriverLocation(request);
        call.enqueue(new Callback<DriverLocationUpdateResponseDTO>() {
            @Override
            public void onResponse(Call<DriverLocationUpdateResponseDTO> call,
                                   Response<DriverLocationUpdateResponseDTO> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                } else {
                    Log.w(TAG, "Failed to update location: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationUpdateResponseDTO> call, Throwable t) {
                Log.e(TAG, "Error updating location", t);
            }
        });
    }

    private void checkWaypointCompletion(GeoPoint currentLocation) {
        if (currentRide.getRoute().getWaypoints() == null) return;

        for (WaypointDTO wp : currentRide.getRoute().getWaypoints()) {
            if (!wp.getIsCompleted() && routeSimulator.isNearPoint(wp.getLocation(), WAYPOINT_THRESHOLD)) {
                wp.setIsCompleted(true);
                Log.d(TAG, "Waypoint " + wp.getOrder() + " completed");
            }
        }
    }
    private void onSimulationComplete() {
        Toast.makeText(requireContext(), "Arrived at destination!", Toast.LENGTH_SHORT).show();
    }

    private void stopSimulation() {
        if (simulationHandler != null && simulationRunnable != null) {
            simulationHandler.removeCallbacks(simulationRunnable);
            Log.d(TAG, "Simulation stopped");
        }
    }

    private void showNoRideMessage() {
        noRideMessage.setVisibility(View.VISIBLE);
        infoCard.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }


    private void onPrimaryActionClick() {
        Toast.makeText(requireContext(), "Primary action clicked", Toast.LENGTH_SHORT).show();
    }

    private void onSecondaryActionClick() {
        Toast.makeText(requireContext(), "Secondary action clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        stopSimulation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSimulation();
        if (mapManager != null) {
            mapManager.clearAll();
        }
    }
}