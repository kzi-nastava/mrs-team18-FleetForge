package com.ognjen.fleetforge.fragments.admin;

import android.app.AlertDialog;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.AdminHistoryAdapter;
import com.ognjen.fleetforge.dtos.admin.AdminRideHistoryDto;
import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.viewmodels.AdminHistoryViewModel;

import java.util.ArrayList;

public class AdminHistoryFragment extends Fragment{

    private ListView historyList;

    private AdminHistoryAdapter adapter;
    private ArrayList<AdminRideHistoryDto> rides = new ArrayList<>();
    private AdminHistoryViewModel viewModel;

    private String currentSortBy = "startTime";
    private String currentDirection = "desc";
    private String dateFrom = null;
    private String dateTo = null;
    private boolean isAscending = false;
    private String usernameFilter = null;
    private ArrayAdapter<String> usernameAdapter;


    public AdminHistoryFragment() {
    }

    public static AdminHistoryFragment newInstance() {
        AdminHistoryFragment fragment = new AdminHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(AdminHistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_history, container, false);

        historyList = view.findViewById(R.id.history_list);
        Button btnNext = view.findViewById(R.id.btn_next_page);
        Button btnPrev = view.findViewById(R.id.btn_prev_page);

        AutoCompleteTextView spinnerSortBy = view.findViewById(R.id.spinner_sort_by);
        ImageButton btnDirection = view.findViewById(R.id.btn_sort_direction);
        TextInputEditText etFrom = view.findViewById(R.id.et_date_from);
        TextInputEditText etTo = view.findViewById(R.id.et_date_to);
        AutoCompleteTextView etUsername = view.findViewById(R.id.et_username);


        adapter = new AdminHistoryAdapter(getActivity(), rides);
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

        usernameAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );

        etUsername.setAdapter(usernameAdapter);
        etUsername.setThreshold(1);

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

        etUsername.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    viewModel.searchUsersByPrefix(s.toString())
                            .observe(getViewLifecycleOwner(), users -> {
                                if (users != null) {
                                    usernameAdapter.clear();
                                    usernameAdapter.addAll(users);
                                    usernameAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etUsername.setOnItemClickListener((parent, view1, position, id) -> {
            usernameFilter = usernameAdapter.getItem(position);
            refreshRides();
        });


        btnNext.setOnClickListener(v -> {
            viewModel.nextPage();
            loadRides();
        });

        btnPrev.setOnClickListener(v -> {
            viewModel.prevPage();
            loadRides();
        });

        adapter.setOnActionListener(new AdminHistoryAdapter.OnActionListener() {

            @Override
            public void onDetailsClicked(AdminRideHistoryDto ride) {
                handleOnDetails(ride);
            }
        });

        return view;
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

    private void handleOnDetails(AdminRideHistoryDto ride) {
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
        viewModel.getRides(dateFrom, dateTo, currentSortBy, currentDirection, usernameFilter).observe(getViewLifecycleOwner(), response -> {    if (response != null && response.getContent() != null) {
                rides.clear();
                rides.addAll(response.getContent());
                adapter.notifyDataSetChanged();
                updatePaginationUI(response);
            }
        });
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

}