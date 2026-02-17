package com.ognjen.fleetforge.fragments.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.ActiveRidesAdapter;
import com.ognjen.fleetforge.dtos.admin.ActiveRideDTO;
import com.ognjen.fleetforge.viewmodels.AdminActiveRidesViewModel;

import org.osmdroid.config.Configuration;

import java.util.ArrayList;

public class AdminActiveRidesFragment extends Fragment {

    private static final int POLLING_INTERVAL_MS = 2000;

    private ListView activeRidesList;
    private ActiveRidesAdapter adapter;
    private final ArrayList<ActiveRideDTO> rides = new ArrayList<>();
    private final ArrayList<ActiveRideDTO> filteredRides = new ArrayList<>();
    private TextView emptyMessage;
    private TextInputEditText searchDriver;

    private AdminActiveRidesViewModel viewModel;

    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private Long currentExpandedRideId = null;

    public AdminActiveRidesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminActiveRidesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        return inflater.inflate(R.layout.fragment_admin_active_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupSearch();
        loadActiveRides();
    }

    private void initializeViews(View view) {
        activeRidesList = view.findViewById(R.id.active_rides_list);
        emptyMessage = view.findViewById(R.id.empty_message);
        searchDriver = view.findViewById(R.id.et_search_driver);

        adapter = new ActiveRidesAdapter(getActivity(), filteredRides);
        activeRidesList.setAdapter(adapter);

        adapter.setOnDetailsClickListener(ride -> {
            currentExpandedRideId = ride.getRideId();
            adapter.setExpandedRideId(ride.getRideId());
            startPolling();
            fetchRideDetails(ride.getRideId());
        });
    }

    private void setupSearch() {
        searchDriver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRides(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterRides(String query) {
        filteredRides.clear();
        if (query.isEmpty()) {
            filteredRides.addAll(rides);
        } else {
            String lowerQuery = query.toLowerCase();
            for (ActiveRideDTO ride : rides) {
                String driverName = (ride.getDriverFirstName() + " " + ride.getDriverLastName()).toLowerCase();
                if (driverName.contains(lowerQuery)) {
                    filteredRides.add(ride);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadActiveRides() {
        viewModel.getAllActiveRides().observe(getViewLifecycleOwner(), activeRides -> {
            if (activeRides != null && !activeRides.isEmpty()) {
                rides.clear();
                rides.addAll(activeRides);
                filterRides(searchDriver.getText() != null ? searchDriver.getText().toString() : "");
            } else {
                rides.clear();
                filteredRides.clear();
                adapter.notifyDataSetChanged();
            }
            updateEmptyState();
        });
    }

    private void updateEmptyState() {
        if (filteredRides.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            activeRidesList.setVisibility(View.GONE);
            if (searchDriver.getText() != null && !searchDriver.getText().toString().isEmpty()) {
                emptyMessage.setText("No rides found for '" + searchDriver.getText().toString() + "'");
            } else {
                emptyMessage.setText("No active rides at the moment");
            }
        } else {
            emptyMessage.setVisibility(View.GONE);
            activeRidesList.setVisibility(View.VISIBLE);
        }
    }

    private void startPolling() {
        stopPolling();

        if (pollingHandler == null) {
            pollingHandler = new Handler(Looper.getMainLooper());
        }

        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentExpandedRideId != null) {
                    fetchRideDetails(currentExpandedRideId);
                    pollingHandler.postDelayed(this, POLLING_INTERVAL_MS);
                }
            }
        };

        pollingHandler.post(pollingRunnable);
    }

    private void stopPolling() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
    }

    private void fetchRideDetails(Long rideId) {
        viewModel.getActiveRideDetails(rideId).observe(getViewLifecycleOwner(), details -> {
            if (details != null) {
                adapter.setDetailedData(details);
            } else {
                stopPolling();
                currentExpandedRideId = null;
                loadActiveRides();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPolling();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentExpandedRideId != null) {
            startPolling();
        }
        loadActiveRides();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
    }
}

