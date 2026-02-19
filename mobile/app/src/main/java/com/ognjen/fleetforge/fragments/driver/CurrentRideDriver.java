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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateRequestDTO;
import com.ognjen.fleetforge.dtos.driver.DriverLocationUpdateResponseDTO;
import com.ognjen.fleetforge.dtos.ride.FinishRideResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;
import com.ognjen.fleetforge.dtos.ride.WaypointDTO;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.api.RideService;
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
    private LinearLayout noRideMessage;

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
    private RideService rideService;

    private RideTrackingDTO currentRide;
    private CalculatedRoute calculatedRoute;
    private View infoCard;

    private RouteSimulator routeSimulator;
    private Handler simulationHandler;
    private Runnable simulationRunnable;
    private static final int SIMULATION_INTERVAL_MS = 2000; // 2 seconds
    private static final int SIMULATION_STEP_SIZE = 5; // Skip 5 coordinates per update
    private static final double WAYPOINT_THRESHOLD = 0.0005; // ~50 meters

    private CurrentRideDriverViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel=new ViewModelProvider(this).get(CurrentRideDriverViewModel.class);
    }
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

        viewModel.getCancelRideObservable().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                handleRideCancelled();
            } else {
                showError("Failed to cancel ride");
            }
        });

        viewModel.getFinishRideObservable().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                handleRideFinished(response);
            } else {
                showError("Failed to finish ride");
            }
        });

        viewModel.getPanicObservable().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                if (response.isSuccess()) {
                    Toast.makeText(requireContext(),
                            response.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    showError(response.getMessage());
                }
            } else {
                showError("Failed to trigger panic.");
            }
        });


        fetchActiveRide();
    }

    private void handleRideFinished(FinishRideResponseDTO response) {
        stopSimulation();
        showFinishRideDialog(response);

        currentRide = null;
        calculatedRoute = null;
        routeSimulator = null;

        if (mapManager != null) {
            mapManager.clearAll();
        }

        if (response.getNextRide() != null) {
            Toast.makeText(requireContext(), "Next ride assigned!", Toast.LENGTH_SHORT).show();
            fetchActiveRide();
        } else {
            navigateToDashboard();
        }
    }

    private void initializeViews(View view) {

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
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        mapManager = new MapManager(mapView, requireContext());
        mapManager.centerOnDefault();
    }

    private void initializeServices() {
        rideService = RetrofitClient.getInstance().getRideService();
        routingService = new RoutingService(MAPBOX_API_KEY);
        simulationHandler = new Handler(Looper.getMainLooper());
    }

    private void fetchActiveRide() {

        Call<RideTrackingDTO> call = rideService.getActiveRideTracking();
        call.enqueue(new Callback<RideTrackingDTO>() {
            @Override
            public void onResponse(Call<RideTrackingDTO> call,
                                   Response<RideTrackingDTO> response) {

                if (response.code() == 204) {
                    showNoRideMessage();
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    currentRide = response.body();
                    displayRideData();
                    calculateAndDisplayRoute();
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

    }

    private void displayRouteInfo() {
        if (currentRide.getRoute() == null) return;

        departureAddress.setText(currentRide.getRoute().getStartAddress());
        destinationAddress.setText(currentRide.getRoute().getEndAddress());

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


        } else if ("IN_PROGRESS".equals(status)) {
            btnPrimaryAction.setText("Finish Ride");
            btnPrimaryAction.setBackgroundTintList(
                    getResources().getColorStateList(R.color.success, null));

            btnSecondaryAction.setText("SOS");
            btnSecondaryAction.setBackgroundTintList(
                    getResources().getColorStateList(R.color.panic, null));

        }
    }

    private void calculateAndDisplayRoute() {

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

        simulationRunnable = new Runnable() {
            @Override
            public void run() {
                if (routeSimulator == null || routeSimulator.isComplete()) {
                    Log.d(TAG, "Simulation complete - arrived at destination");
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

        Call<DriverLocationUpdateResponseDTO> call = rideService.updateDriverLocation(request);
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
        if (currentRide == null) {
            Toast.makeText(requireContext(), "Ride data is still loading or unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(btnPrimaryAction.getText().equals("Start Ride")){
            viewModel.startRide(currentRide.getRideId()).observe(getViewLifecycleOwner(), response -> {
                if(response != null){
                    fetchActiveRide();
                    Toast.makeText(requireContext(), "Ride started!", Toast.LENGTH_SHORT).show();
                }
            });
        } else if(btnPrimaryAction.getText().equals("Finish Ride")){
            viewModel.finishRide(currentRide.getRideId());
        }
    }

    private void showFinishRideDialog(FinishRideResponseDTO response) {
        String message = "Total price: " +
                String.format(Locale.getDefault(), "%.2f RSD", response.getTotalCost());

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Ride Completed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }


    private void onSecondaryActionClick() {
        String currentText = btnSecondaryAction.getText().toString();

        if ("Cancel".equalsIgnoreCase(currentText)) {
            showCancelConfirmationDialog();
        } else if ("SOS".equalsIgnoreCase(currentText)) {
            if (currentRide != null) {
                viewModel.triggerPanic(currentRide.getRideId());
            }
        }
    }

    private void showCancelConfirmationDialog() {
        if (currentRide == null) return;

        final android.widget.EditText inputReason = new android.widget.EditText(requireContext());
        inputReason.setHint("Enter reason (e.g., Vehicle breakdown, Traffic)");

        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        android.widget.FrameLayout.LayoutParams params = new  android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginInDp = 16;
        int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);

        params.leftMargin = marginInPx;
        params.rightMargin = marginInPx;
        inputReason.setLayoutParams(params);
        container.addView(inputReason);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancel Ride")
                .setMessage("Please provide a reason for cancellation:")
                .setView(container) // Add the EditText container to the dialog
                .setPositiveButton("Confirm Cancellation", (dialog, which) -> {
                    String reason = inputReason.getText().toString().trim();

                    if (reason.isEmpty()) {
                        Toast.makeText(requireContext(), "Reason is required!", Toast.LENGTH_SHORT).show();
                    } else {
                        performCancelRide(reason);
                    }
                })
                .setNegativeButton("Back", null)
                .show();
    }

    private void performCancelRide(String reason) {
        if (currentRide != null) {
            viewModel.cancelRide(currentRide.getRideId(), reason);
        }
    }

    private void handleRideCancelled() {
        Toast.makeText(requireContext(), "Ride cancelled successfully", Toast.LENGTH_SHORT).show();
        stopSimulation();
        currentRide = null;
        if (mapManager != null) {
            mapManager.clearAll();
        }
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profile);
        }
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