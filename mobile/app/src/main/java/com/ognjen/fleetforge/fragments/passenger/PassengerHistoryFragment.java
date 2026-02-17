package com.ognjen.fleetforge.fragments.passenger;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.PassengerHistoryAdapter;
import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PassengerHistoryFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private static final int SHAKE_THRESHOLD = 800;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private static final int SHAKE_COOLDOWN = 1500;
    private long lastShakeTime = 0;

    private ListView historyList;

    private PassengerHistoryAdapter adapter;
    private ArrayList<PassengerRideHistoryDto> rides = new ArrayList<>();
    private PassengerHistoryViewModel viewModel;

    private Set<Long> favoriteRideIds = new HashSet<>();
    private Map<Long, Long> rideIdToFavoriteId = new HashMap<>();

    private String currentSortBy = "startTime";
    private String currentDirection = "desc";
    private String dateFrom = null;
    private String dateTo = null;
    private boolean isAscending = false;

    public PassengerHistoryFragment() {
    }

    public static PassengerHistoryFragment newInstance(String param1, String param2) {
        PassengerHistoryFragment fragment = new PassengerHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(PassengerHistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        View view = inflater.inflate(R.layout.fragment_passenger_history, container, false);

        historyList = view.findViewById(R.id.history_list);
        Button btnNext = view.findViewById(R.id.btn_next_page);
        Button btnPrev = view.findViewById(R.id.btn_prev_page);

        AutoCompleteTextView spinnerSortBy = view.findViewById(R.id.spinner_sort_by);
        ImageButton btnDirection = view.findViewById(R.id.btn_sort_direction);
        TextInputEditText etFrom = view.findViewById(R.id.et_date_from);
        TextInputEditText etTo = view.findViewById(R.id.et_date_to);


        adapter = new PassengerHistoryAdapter(getActivity(), rides);
        historyList.setAdapter(adapter);

        loadRides();

        String[] options = {"startTime", "endTime", "startAddress", "endAddress","status"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, options);
        spinnerSortBy.setAdapter(sortAdapter);
        spinnerSortBy.setText(options[0], false);


        spinnerSortBy.setOnItemClickListener((parent, v, position, id) -> {
            currentSortBy = options[position];
            refreshRides();
        });

        btnDirection.setOnClickListener(v -> {
            isAscending = !isAscending;
            currentDirection = isAscending ? "asc" : "desc";
            btnDirection.setImageResource(isAscending ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
            refreshRides();
        });

        etFrom.setOnClickListener(v -> showDatePicker(date -> {
            etFrom.setText(date);
            dateFrom = formatToApiTimestamp(date);
            refreshRides();
        }));

        etTo.setOnClickListener(v -> showDatePicker(date -> {
            etTo.setText(date);
            dateTo = formatToApiTimestamp(date);
            refreshRides();
        }));

        btnNext.setOnClickListener(v -> {
            viewModel.nextPage();
            loadRides();
        });

        btnPrev.setOnClickListener(v -> {
            viewModel.prevPage();
            loadRides();
        });

        adapter.setOnActionListener(new PassengerHistoryAdapter.OnActionListener() {
            @Override
            public void onHeart(PassengerRideHistoryDto ride, ImageButton heartBtn) {
                handleOnHeart(ride, heartBtn);
            }

            @Override
            public void onDetailsClicked(PassengerRideHistoryDto ride) {
                handleOnDetails(ride);
            }
        });

        viewModel.getFavorites().observe(getViewLifecycleOwner(),response -> {
            if(response!=null){
                favoriteRideIds.clear();
                rideIdToFavoriteId.clear();

                for(FavoriteRouteGetResponseDTO fav : response) {
                    Long rideId = fav.getRideId();
                    Long favoriteId = fav.getId();

                    if(rideId != null) {
                        favoriteRideIds.add(rideId);
                        rideIdToFavoriteId.put(rideId, favoriteId);
                    }
                }
                adapter.setFavoriteIds(favoriteRideIds);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getRides().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getContent() != null) {
                rides.clear();
                rides.addAll(response.getContent());
                adapter.notifyDataSetChanged();
                updatePaginationUI(response);
            }
        });
    }


    private String formatToApiTimestamp(String date) {
        if (date == null || date.isEmpty()) return null;
        return date + "T00:00:00.000Z";
    }

    private void showDatePicker(OnDateSelectedListener listener) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            listener.onDateSelected(selectedDate);
        }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    interface OnDateSelectedListener {
        void onDateSelected(String date);
    }

    private void handleOnDetails(PassengerRideHistoryDto ride) {
        viewModel.getRideDetails(ride.getRideId()).observe(getViewLifecycleOwner(), details -> {
            if (details != null) {
                adapter.setDetailedData(details);
                adapter.setExpandedRideId(ride.getRideId());
            } else {
                Toast.makeText(getContext(), "Failed to load ride details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshRides() {
        viewModel.resetPage();
        loadRides();
    }

    private void loadRides() {
        viewModel.fetchRides(dateFrom, dateTo, currentSortBy, currentDirection);
    }

    private void updatePaginationUI(PageResponse<?> response) {
        View view = getView();
        if (view == null) return;

        TextView pageInfo = view.findViewById(R.id.tv_pagination_info);
        Button btnNext = view.findViewById(R.id.btn_next_page);
        Button btnPrev = view.findViewById(R.id.btn_prev_page);

        pageInfo.setText("Page " + (response.getNumber() + 1) + " of " + response.getTotalPages());

        btnPrev.setEnabled(!response.isFirst());
        btnNext.setEnabled(!response.isLast());
    }

    private void handleOnHeart(PassengerRideHistoryDto ride, ImageButton heartBtn) {
        if (heartBtn.isSelected()) {
            viewModel.deleteFavorite(rideIdToFavoriteId.get(ride.getRideId())).observe(getViewLifecycleOwner(), success -> {
                if (success) {
                    heartBtn.setSelected(false);
                    favoriteRideIds.remove(ride.getRideId());
                    Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showFavoriteRouteDialog(routeName -> {
                viewModel.addFavorite(routeName, ride.getRideId()).observe(getViewLifecycleOwner(), success -> {
                    if (success) {
                        heartBtn.setSelected(true);
                        favoriteRideIds.add(ride.getRideId());
                        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
    interface OnRouteNameEnteredListener {
        void onNameEntered(String routeName);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager != null) {
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z)
                        / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    if (curTime - lastShakeTime > SHAKE_COOLDOWN) {
                        lastShakeTime = curTime;

                        currentSortBy = "startTime";

                        AutoCompleteTextView spinnerSortBy = getView().findViewById(R.id.spinner_sort_by);
                        spinnerSortBy.setText("startTime", false);

                        ImageButton btnDirection = getView().findViewById(R.id.btn_sort_direction);
                        isAscending = !isAscending;
                        currentDirection = isAscending ? "asc" : "desc";
                        btnDirection.setImageResource(isAscending ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);

                        refreshRides();

                        Toast.makeText(getContext(),
                                "Sorted by Start Time",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }


    private void showFavoriteRouteDialog(OnRouteNameEnteredListener listener) {
        final EditText input = new EditText(getContext());
        input.setHint("e.g. Work, Home...");
        input.requestFocus();
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        lp.setMargins(50, 20, 50, 0);
        input.setLayoutParams(lp);
        container.addView(input);

        new AlertDialog.Builder(getContext())
                .setTitle("Enter Route Name")
                .setMessage("Give a name to your favorite route:")
                .setView(container)
                .setPositiveButton("OK", (dialog, which) -> {
                    String value = input.getText().toString();
                    if (listener != null) {
                        listener.onNameEntered(value);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}