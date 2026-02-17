package com.ognjen.fleetforge.fragments.passenger;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dialogs.RateRideDialog;
import com.ognjen.fleetforge.dialogs.ReportInconsistencyDialog;
import com.ognjen.fleetforge.dtos.ride.InconsistencyReportRequestDTO;
import com.ognjen.fleetforge.dtos.ride.InconsistencyReportResponseDTO;
import com.ognjen.fleetforge.dtos.ride.RideTrackingDTO;
import com.ognjen.fleetforge.dtos.ride.WaypointDTO;
import com.ognjen.fleetforge.dtos.ride.RideReviewRequestDTO;
import com.ognjen.fleetforge.dtos.ride.RideReviewResponseDTO;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.model.GeoPoint;
import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.api.RideService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.utils.MapManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentRidePassenger extends Fragment {

    private static final String TAG = "CurrentRidePassenger";
    private static final String MAPBOX_API_KEY = BuildConfig.MAPBOX_API_KEY;
    private static final int POLLING_INTERVAL_MS = 2000;

    private MapView mapView;
    private FrameLayout loadingOverlay;
    private TextView noRideMessage;

    // TODO place real driver image
    private ImageView driverProfileImage;
    private TextView driverName;
    private TextView driverPhone;

    private TextView departureAddress;
    private TextView destinationAddress;
    private TextView distanceText;
    private TextView timeText;

    // TODO  implement buttons logic
    private Button btnReportInconsistency;
    private Button btnSOS;
    private View rideActionButtons;
    private View buttonsDivider;

    private View infoCard;

    private MapManager mapManager;
    private RoutingService routingService;
    private RideService rideService;
    private CurrentRidePassengerViewModel viewModel;

    private RideTrackingDTO currentRide;
    private CalculatedRoute calculatedRoute;
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private boolean isPolling = false;
    private Marker driverCarMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CurrentRidePassengerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.current_ride_passenger_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        initializeMap();
        initializeServices();
        observeViewModel();

        fetchActiveRide();
    }

    private void initializeViews(View view) {
        mapView = view.findViewById(R.id.map_view);
        loadingOverlay = view.findViewById(R.id.loading_overlay);
        noRideMessage = view.findViewById(R.id.no_ride_message);
        infoCard = view.findViewById(R.id.info_card);

        View driverInfoSection = view.findViewById(R.id.driver_info_section);
        driverProfileImage = driverInfoSection.findViewById(R.id.driver_profile_image);
        driverName = driverInfoSection.findViewById(R.id.driver_name);
        driverPhone = driverInfoSection.findViewById(R.id.driver_phone);

        View routeInfoSection = view.findViewById(R.id.route_info_section);
        departureAddress = routeInfoSection.findViewById(R.id.departure_address);
        destinationAddress = routeInfoSection.findViewById(R.id.destination_address);
        distanceText = routeInfoSection.findViewById(R.id.distance_text);
        timeText = routeInfoSection.findViewById(R.id.time_text);

        btnReportInconsistency = view.findViewById(R.id.btn_report_inconsistency);
        btnSOS = view.findViewById(R.id.btn_sos);
        rideActionButtons = view.findViewById(R.id.buttons_container);
        buttonsDivider = view.findViewById(R.id.buttons_divider);

        // TODO implement button listeners
        btnReportInconsistency.setOnClickListener(v -> onReportInconsistencyClick());
        btnSOS.setOnClickListener(v -> onSOSClick());
    }

    private void initializeMap() {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        mapManager = new MapManager(mapView, requireContext());
        mapManager.centerOnDefault();
    }

    private void initializeServices() {
        routingService = new RoutingService(MAPBOX_API_KEY);
        rideService = RetrofitClient.getInstance().getRideService();
        pollingHandler = new Handler(Looper.getMainLooper());
    }

    private void observeViewModel() {
        viewModel.getRideTrackingData().observe(getViewLifecycleOwner(), this::handleRideTrackingUpdate);

        viewModel.getNoActiveRide().observe(getViewLifecycleOwner(), noRide -> {
            if (noRide != null && noRide) {
                showNoRideMessage();
                stopPolling();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showError(error);
            }
        });
    }

    private void fetchActiveRide() {
        viewModel.fetchActiveRideTracking();
    }

    private void handleRideTrackingUpdate(RideTrackingDTO tracking) {
        if (tracking == null) return;

        if ("COMPLETED".equals(tracking.getStatus())) {
            stopPolling();
            showRatingDialog(tracking);
            return;
        }

        String previousStatus = currentRide != null ? currentRide.getStatus() : null;

        currentRide = tracking;

        if (!tracking.getStatus().equals(previousStatus)) {
            displayRideData();
            driverCarMarker = null;
        }

        calculateAndDisplayRoute();

        if (!isPolling) {
            startPolling();
        }

        loadingOverlay.setVisibility(View.GONE);
        noRideMessage.setVisibility(View.GONE);
        infoCard.setVisibility(View.VISIBLE);
    }

    private void displayRideData() {
        if (currentRide == null) return;

        displayDriverInfo();
        displayRouteInfo();
        configureButtons();
    }

    private void displayDriverInfo() {
        if (currentRide.getDriver() == null) return;

        String fullName = currentRide.getDriver().getFirstName() + " " +
                currentRide.getDriver().getLastName();
        driverName.setText(fullName);
        driverPhone.setText(currentRide.getDriver().getPhoneNumber());
    }

    private void displayRouteInfo() {
        if (currentRide.getRoute() == null) return;

        departureAddress.setText(currentRide.getRoute().getStartAddress());
        destinationAddress.setText(currentRide.getRoute().getEndAddress());
    }

    private void configureButtons() {
        String status = currentRide.getStatus();

        if ("IN_PROGRESS".equals(status)) {
            rideActionButtons.setVisibility(View.VISIBLE);
            buttonsDivider.setVisibility(View.VISIBLE);
        } else {
            rideActionButtons.setVisibility(View.GONE);
            buttonsDivider.setVisibility(View.GONE);
        }
    }

    private void calculateAndDisplayRoute() {
        if (currentRide == null || currentRide.getRoute() == null) return;

        List<GeoPoint> routePoints = new ArrayList<>();
        GeoPoint currentLocation = currentRide.getCurrentLocation();
        String status = currentRide.getStatus();

        routePoints.add(currentLocation);

        if ("ACCEPTED".equals(status)) {
            routePoints.add(currentRide.getRoute().getStartLocation());

        } else if ("IN_PROGRESS".equals(status)) {
            if (currentRide.getRoute().getWaypoints() != null) {
                for (WaypointDTO wp : currentRide.getRoute().getWaypoints()) {
                    if (!wp.getIsCompleted()) {
                        routePoints.add(wp.getLocation());
                    }
                }
            }
            routePoints.add(currentRide.getRoute().getEndLocation());
        }

        new Thread(() -> {
            try {
                calculatedRoute = routingService.calculateRoute(routePoints);

                new Handler(Looper.getMainLooper()).post(() -> {
                    displayRouteOnMap();
                    updateDistanceAndTime();
                });

            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        showError("Failed to calculate route")
                );
            }
        }).start();
    }

    private void displayRouteOnMap() {
        if (calculatedRoute == null) return;

        driverCarMarker = null;

        mapManager.clearAll();

        List<org.osmdroid.util.GeoPoint> osmPoints = new ArrayList<>();
        for (GeoPoint point : calculatedRoute.getCoordinates()) {
            osmPoints.add(new org.osmdroid.util.GeoPoint(
                    point.getLatitude(),
                    point.getLongitude()
            ));
        }
        mapManager.drawRoute(osmPoints, 0xFFFF9900);

        addLocationMarkers();

        updateDriverCarMarker();
    }

    private void addLocationMarkers() {
        if (currentRide == null) return;

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
    }

    private void updateDriverCarMarker() {
        if (currentRide == null || currentRide.getCurrentLocation() == null) return;

        GeoPoint currentLocation = currentRide.getCurrentLocation();
        org.osmdroid.util.GeoPoint newPosition = new org.osmdroid.util.GeoPoint(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        if (driverCarMarker == null) {
            driverCarMarker = mapManager.addMarker(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    "Driver Location",
                    R.drawable.ic_current_taxi
            );
        } else {
            driverCarMarker.setPosition(newPosition);
            mapView.invalidate();
        }
    }

    private void updateDistanceAndTime() {
        if (calculatedRoute == null) return;

        distanceText.setText(String.format(Locale.getDefault(),
                "%.1f km", calculatedRoute.getDistanceKm()));
        timeText.setText(calculatedRoute.getEstimatedMinutes() + " min");
    }

    private void startPolling() {
        if (isPolling) return;

        isPolling = true;
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPolling) {
                    fetchActiveRide();
                    pollingHandler.postDelayed(this, POLLING_INTERVAL_MS);
                }
            }
        };

        pollingHandler.postDelayed(pollingRunnable, POLLING_INTERVAL_MS);
    }

    private void stopPolling() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
            isPolling = false;
            Log.d(TAG, "Stopped polling");
        }
    }

    private void showRatingDialog(RideTrackingDTO ride) {
        String routeInfo = ride.getRoute().getStartAddress() + " → " + ride.getRoute().getEndAddress();

        RateRideDialog dialog = new RateRideDialog(requireContext(), routeInfo, new RateRideDialog.OnRatingSubmitListener() {
            @Override
            public void onSubmit(int driverRating, int vehicleRating, String comment) {
                submitReview(ride.getRideId(), driverRating, vehicleRating, comment);
                navigateToDashboard();
            }

            @Override
            public void onNotNow() {
                navigateToDashboard();
            }
        });

        dialog.show();
    }

    private void submitReview(Long rideId, int driverRating, int vehicleRating, String comment) {
        RideReviewRequestDTO request = new RideReviewRequestDTO(vehicleRating, driverRating, comment);

        rideService.createReview(rideId, request).enqueue(new Callback<RideReviewResponseDTO>() {
            @Override
            public void onResponse(Call<RideReviewResponseDTO> call, Response<RideReviewResponseDTO> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RideReviewResponseDTO> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;

                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDashboard() {
        currentRide = null;
        if (mapManager != null) {
            mapManager.clearAll();
        }
        driverCarMarker = null;

        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    private void onReportInconsistencyClick() {

        ReportInconsistencyDialog dialog = new ReportInconsistencyDialog(requireContext(), this::submitInconsistencyReport);

        dialog.show();
    }

    private void submitInconsistencyReport(String comment) {
        GeoPoint currentLocation = currentRide.getCurrentLocation();
        InconsistencyReportRequestDTO request = new InconsistencyReportRequestDTO(comment, currentLocation);

        rideService.reportInconsistency(request).enqueue(new Callback<InconsistencyReportResponseDTO>() {
            @Override
            public void onResponse(Call<InconsistencyReportResponseDTO> call, Response<InconsistencyReportResponseDTO> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to submit report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InconsistencyReportResponseDTO> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;

                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onSOSClick() {
        Toast.makeText(requireContext(), "SOS - To be implemented", Toast.LENGTH_SHORT).show();
        // TODO: Implement SOS functionality
    }

    private void showNoRideMessage() {
        noRideMessage.setVisibility(View.VISIBLE);
        infoCard.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        // Resume polling if we have an active ride
        if (currentRide != null && !isPolling) {
            startPolling();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        stopPolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
        if (mapManager != null) {
            mapManager.clearAll();
        }
        driverCarMarker = null;
    }
}