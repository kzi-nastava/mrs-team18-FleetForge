package com.ognjen.fleetforge.fragments.passenger;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.PassengerHistoryAdapter;
import com.ognjen.fleetforge.dtos.passenger.FavoriteRouteGetResponseDTO;
import com.ognjen.fleetforge.model.PassengerHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PassengerHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PassengerHistoryFragment extends Fragment {

    private ListView historyList;

    private PassengerHistoryAdapter adapter;
    private ArrayList<PassengerHistory> rides= new ArrayList<>();
    private PassengerHistoryViewModel viewModel;

    private Set<Long> favoriteRideIds = new HashSet<>();
    private Map<Long, Long> rideIdToFavoriteId = new HashMap<>();
    private PassengerHistoryFragment.OnRouteNameEnteredListener listener;
    public static class TempClass{
        Long id;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PassengerHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PassengerHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static PassengerHistoryFragment newInstance(String param1, String param2) {
        PassengerHistoryFragment fragment = new PassengerHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(PassengerHistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_history, container, false);

        historyList = view.findViewById(R.id.history_list);
        rides.clear();


        for (long i = 1; i <= 5; i++) {
            PassengerHistory dummyRide = new PassengerHistory();
            dummyRide.setRideId(i);
            rides.add(dummyRide);
        }

        adapter = new PassengerHistoryAdapter(getActivity(), rides);
        historyList.setAdapter(adapter);

        adapter.setOnActionListener(new PassengerHistoryAdapter.OnActionListener() {
            @Override
            public void onHeart(PassengerHistory ride, ImageButton heartBtn) {
                handleOnHeart(ride,heartBtn);
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

    private void handleOnHeart(PassengerHistory ride, ImageButton heartBtn){
        if(heartBtn.isSelected()){

            viewModel.deleteFavorite(rideIdToFavoriteId.get(ride.getRideId())).observe(getViewLifecycleOwner(), success -> {
                if(success) {
                    heartBtn.setSelected(false);
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error removing favorite", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            showFavoriteRouteDialog(routeName -> {
                if (routeName.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.addFavorite(routeName, ride.getRideId()).observe(getViewLifecycleOwner(), newFavoriteId -> {
                    if (newFavoriteId != null) {
                        heartBtn.setSelected(true);

                        Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error adding to favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        }
    }
    interface OnRouteNameEnteredListener {
        void onNameEntered(String routeName);
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