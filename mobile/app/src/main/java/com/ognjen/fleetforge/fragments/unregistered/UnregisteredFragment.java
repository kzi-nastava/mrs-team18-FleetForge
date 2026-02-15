package com.ognjen.fleetforge.fragments.unregistered;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.activities.auth.LoginActivity;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.photon.PhotonResponse;
import com.ognjen.fleetforge.model.VehicleLocation;
import com.ognjen.fleetforge.utils.MapManager;
import com.ognjen.fleetforge.viewmodels.UnregisteredHomeViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnregisteredFragment extends Fragment {
    private static final String TAG = "UnregisteredFragment";

    private MapView mapView;
    private MapManager mapManager;

    private MaterialAutoCompleteTextView startLocation;
    private MaterialAutoCompleteTextView endLocation;
    private Button orderBtn;
    private CardView locationCard;
    private CardView infoCard;
    private TextView tvDistance, tvDuration, tvPrice;

    private UnregisteredHomeViewModel viewModel;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UnregisteredHomeViewModel.class);
        viewModel.init(BuildConfig.MAPBOX_API_KEY);

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_unregistered, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);
        startLocation = view.findViewById(R.id.et_start_location);
        endLocation = view.findViewById(R.id.et_end_location);
        orderBtn = view.findViewById(R.id.btn_order);
        locationCard = view.findViewById(R.id.location_card);

        infoCard = view.findViewById(R.id.ride_info_card);
        tvDistance = view.findViewById(R.id.tv_distance);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvPrice = view.findViewById(R.id.tv_price);

        mapManager = new MapManager(mapView, requireContext());
        mapManager.centerOnDefault();

        observeViewModel();
        setupAutocomplete(startLocation);
        setupAutocomplete(endLocation);
        setupSelectionListeners();

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            boolean isKeyboardVisible =
                    insets.isVisible(WindowInsetsCompat.Type.ime());

            updateCardPosition(isKeyboardVisible);

            return insets;
        });


        orderBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Login required to order ride", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        });
        loadVehicles();
    }
    private void updateCardPosition(boolean isKeyboardVisible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationCard.getLayoutParams();

        if (isKeyboardVisible) {
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            params.topMargin = (int) (32 * getResources().getDisplayMetrics().density);
            params.bottomMargin = 0;
        } else {
            params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            params.bottomMargin = (int) (16 * getResources().getDisplayMetrics().density);
            params.topMargin = 0;
        }

        locationCard.setLayoutParams(params);
    }

    private void hideKeyboard() {
        View view = this.getView();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)
                    requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void observeViewModel() {

        viewModel.getSuggestionsData().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getFeatures() != null) {
                updateDropdown(response);
            }
        });

        viewModel.getRouteData().observe(getViewLifecycleOwner(), calculatedRoute -> {
            if (calculatedRoute != null) {
                List<GeoPoint> osmPoints = new ArrayList<>();
                for (com.ognjen.fleetforge.model.GeoPoint point : calculatedRoute.getCoordinates()) {
                    osmPoints.add(new GeoPoint(point.getLatitude(), point.getLongitude()));
                }
                mapManager.drawRoute(osmPoints, 0xFFFF9800);

                double distanceKm = calculatedRoute.getDistanceKm();
                int durationMin = (int) (calculatedRoute.getEstimatedMinutes());

                tvDistance.setText(String.format("Distance: %.2f km", distanceKm));
                tvDuration.setText(String.format("Duration: %d min", durationMin));
                infoCard.setVisibility(View.VISIBLE);

                viewModel.fetchPrice(distanceKm, "STANDARD");
            }
        });

        viewModel.getEstimatedPrice().observe(getViewLifecycleOwner(), price -> {
            tvPrice.setText(String.format("Price: RSD %.2f", price));
        });

    }

    private void setupAutocomplete(MaterialAutoCompleteTextView field) {

        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                searchHandler.removeCallbacks(searchRunnable);

                if (s.length() > 2) {
                    searchRunnable = () ->
                            viewModel.fetchSuggestions(s.toString());
                    searchHandler.postDelayed(searchRunnable, 500);

                } else if (s.length() == 0) {

                    if (field.getTag() instanceof Marker) {
                        mapManager.removeMarker((Marker) field.getTag());
                        field.setTag(null);
                        mapManager.clearRoute();
                    }
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void setupSelectionListeners() {
        startLocation.setOnItemClickListener((parent, view, position, id) -> {
            PhotonResponse.Feature selected = (PhotonResponse.Feature) parent.getItemAtPosition(position);
            handleLocationSelection(startLocation, selected);

            endLocation.requestFocus();
        });

        endLocation.setOnItemClickListener((parent, view, position, id) -> {
            PhotonResponse.Feature selected = (PhotonResponse.Feature) parent.getItemAtPosition(position);
            handleLocationSelection(endLocation, selected);

            endLocation.clearFocus();
            hideKeyboard();
        });
    }

    private void handleLocationSelection(MaterialAutoCompleteTextView field,
                                         PhotonResponse.Feature selected) {

        double lat = selected.getGeometry().getLat();
        double lon = selected.getGeometry().getLon();
        String name = selected.getProperties().getDisplayName();

        if (field.getTag() instanceof Marker) {
            mapManager.removeMarker((Marker) field.getTag());
        }

        Marker marker = mapManager.addMarker(lat, lon, name,
                R.drawable.ic_map_point);

        field.setTag(marker);
        field.setText(name, false);

        mapView.getController().animateTo(new GeoPoint(lat, lon));

        drawRouteIfReady();
    }

    private void drawRouteIfReady() {

        if (startLocation.getTag() != null &&
                endLocation.getTag() != null) {

            Marker startMarker = (Marker) startLocation.getTag();
            Marker endMarker = (Marker) endLocation.getTag();

            List<com.ognjen.fleetforge.model.GeoPoint> points =
                    new ArrayList<>();

            points.add(new com.ognjen.fleetforge.model.GeoPoint(
                    startMarker.getPosition().getLatitude(),
                    startMarker.getPosition().getLongitude()));

            points.add(new com.ognjen.fleetforge.model.GeoPoint(
                    endMarker.getPosition().getLatitude(),
                    endMarker.getPosition().getLongitude()));

            try {
                viewModel.drawRoute(points);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(),
                        "Failed to calculate route",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDropdown(PhotonResponse response) {

        View focusedView = getActivity().getCurrentFocus();
        if (!(focusedView instanceof MaterialAutoCompleteTextView)) return;

        MaterialAutoCompleteTextView field =
                (MaterialAutoCompleteTextView) focusedView;

        ArrayAdapter<PhotonResponse.Feature> adapter =
                new ArrayAdapter<PhotonResponse.Feature>(
                        getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        response.getFeatures()) {

                    @Override
                    public View getView(int pos, View convert,
                                        ViewGroup parent) {

                        TextView tv = (TextView)
                                super.getView(pos, convert, parent);

                        tv.setText(getItem(pos)
                                .getProperties()
                                .getDisplayName());

                        return tv;
                    }
                };

        field.setAdapter(adapter);

        if (!response.getFeatures().isEmpty()) {
            field.showDropDown();
        }
    }
    private void loadVehicles() {
        Log.d(TAG, "Loading vehicles from backend...");

        RetrofitClient.getInstance()
                .getUnregisteredService()
                .getActiveVehicles()
                .enqueue(new Callback<List<VehicleLocation>>() {
                    @Override
                    public void onResponse(Call<List<VehicleLocation>> call,
                                           Response<List<VehicleLocation>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<VehicleLocation> vehicles = response.body();
                            Log.d(TAG, "Vehicles loaded successfuly"+ vehicles.size());

                            mapManager.displayVehicles(vehicles);

                        } else {
                            Log.e(TAG, "Failed to load vehicles. Response code: " + response.code());
                            showError("Failed to load vehicles");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<VehicleLocation>> call, Throwable t) {
                        Log.e(TAG, "Network error while loading vehicles", t);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void onDestroyView() {
        if (mapManager != null) {
            mapManager.clearVehicleMarkers();
        }
        mapView = null;
        mapManager = null;

        super.onDestroyView();
    }
}