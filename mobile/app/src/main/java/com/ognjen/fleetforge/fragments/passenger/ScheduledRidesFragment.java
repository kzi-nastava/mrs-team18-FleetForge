package com.ognjen.fleetforge.fragments.passenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.ScheduledRidesAdapter;
import com.ognjen.fleetforge.dtos.common.PageResponse;
import com.ognjen.fleetforge.dtos.ride.ScheduledRideDto;
import com.ognjen.fleetforge.viewmodels.ScheduledRidesViewModel;

import java.util.ArrayList;

public class ScheduledRidesFragment extends Fragment {
    private ListView scheduledList;
    private ScheduledRidesAdapter adapter;
    private ArrayList<ScheduledRideDto> rides = new ArrayList<>();
    private ScheduledRidesViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ScheduledRidesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_scheduled_rides, container, false);

        scheduledList = view.findViewById(R.id.scheduled_rides_list);
        Button btnNext = view.findViewById(R.id.btn_next_page);
        Button btnPrev = view.findViewById(R.id.btn_prev_page);

        adapter = new ScheduledRidesAdapter(getActivity(), rides);
        scheduledList.setAdapter(adapter);

        adapter.setOnCancelListener(ride -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Cancel Ride")
                    .setMessage("Are you sure you want to cancel this scheduled ride?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        viewModel.cancelRide(ride.getId());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        loadRides();

        btnNext.setOnClickListener(v -> {
            viewModel.nextPage();
            loadRides();
        });

        btnPrev.setOnClickListener(v -> {
            viewModel.prevPage();
            loadRides();
        });

        viewModel.getCancelResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(),
                            "Ride cancelled successfully",
                            Toast.LENGTH_SHORT).show();

                    loadRides();
                } else {
                    Toast.makeText(getContext(),
                            "Failed to cancel ride",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void loadRides() {
        viewModel.getScheduledRides().observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.getContent() != null) {
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
        pageInfo.setText("Page " + (response.getNumber() + 1) + " of " + response.getTotalPages());

        view.findViewById(R.id.btn_prev_page).setEnabled(!response.isFirst());
        view.findViewById(R.id.btn_next_page).setEnabled(!response.isLast());
    }
}