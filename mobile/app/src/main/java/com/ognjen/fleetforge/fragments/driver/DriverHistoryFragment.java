package com.ognjen.fleetforge.fragments.driver;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.DriverService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.adapters.RideHistoryAdapter;
import com.ognjen.fleetforge.dtos.driver.CompletedRideDTO;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHistoryFragment extends Fragment {

    private static final String TAG = "DriverHistoryFragment";

    private RecyclerView rvRides;
    private LinearLayout llEmptyState;
    private TextInputEditText etDatePicker;
    private Button btnClearFilter;

    private RideHistoryAdapter adapter;
    private List<CompletedRideDTO> allRides;
    private List<CompletedRideDTO> filteredRides;
    private DriverService driverService;

    private String selectedDateForDisplay = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);

        initViews(view);
        setupRecyclerView();
        setupDatePicker();
        initRetrofit();
        loadRideHistory();

        return view;
    }

    private void initViews(View view) {
        rvRides = view.findViewById(R.id.rv_rides);
        llEmptyState = view.findViewById(R.id.ll_empty_state);
        etDatePicker = view.findViewById(R.id.et_date_picker);
        btnClearFilter = view.findViewById(R.id.btn_clear_filter);
    }

    private void setupRecyclerView() {
        allRides = new ArrayList<>();
        filteredRides = new ArrayList<>();
        adapter = new RideHistoryAdapter(filteredRides);

        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRides.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDatePicker.setOnClickListener(v -> showDatePickerDialog());

        btnClearFilter.setOnClickListener(v -> {
            selectedDateForDisplay = null;
            etDatePicker.setText("");
            filterRides();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateForDisplay = String.format(Locale.US, "%d-%02d-%02d",
                            year, month + 1, dayOfMonth);

                    etDatePicker.setText(selectedDateForDisplay);

                    filterRides();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void initRetrofit() {
        driverService = RetrofitClient.getInstance().getDriverService();
    }

    private void loadRideHistory() {

        Call<List<CompletedRideDTO>> call = driverService.getDriverRideHistory();
        call.enqueue(new Callback<List<CompletedRideDTO>>() {
            @Override
            public void onResponse(Call<List<CompletedRideDTO>> call,
                                   Response<List<CompletedRideDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRides = response.body();
                    Log.d(TAG, "Successfully loaded " + allRides.size() + " rides");

                    filterRides();
                } else {
                    Log.e(TAG, "Failed to load ride history. Code: " + response.code());
                    showError("Failed to load ride history");
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<CompletedRideDTO>> call, Throwable t) {
                Log.e(TAG, "Error loading ride history", t);
                showError("Error: " + t.getMessage());
                showEmptyState();
            }
        });
    }

    private void filterRides() {
        if (selectedDateForDisplay == null || selectedDateForDisplay.isEmpty()) {
            filteredRides = new ArrayList<>(allRides);
            Log.d(TAG, "No filter - showing all " + filteredRides.size() + " rides");
        } else {
            // Filter by selected date
            filteredRides = new ArrayList<>();

            for (CompletedRideDTO ride : allRides) {
                String rideDate = ride.getRideDate();

                if (rideDate != null && rideDate.equals(selectedDateForDisplay)) {
                    filteredRides.add(ride);
                    Log.d(TAG, "Match found!");
                }
            }

            Log.d(TAG, "Filtered " + filteredRides.size() + " rides for date: " + selectedDateForDisplay);
        }

        if (filteredRides.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            adapter.updateData(filteredRides);
        }
    }

    private void showEmptyState() {
        rvRides.setVisibility(View.GONE);
        llEmptyState.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        rvRides.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRideHistory();
    }
}