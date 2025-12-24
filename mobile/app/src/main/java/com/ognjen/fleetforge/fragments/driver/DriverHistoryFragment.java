package com.ognjen.fleetforge.fragments.driver;

import com.ognjen.fleetforge.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ognjen.fleetforge.adapters.RideHistoryAdapter;
import com.ognjen.fleetforge.model.Ride;
import java.util.ArrayList;
import java.util.List;


public class DriverHistoryFragment extends Fragment {

    private RecyclerView rvRides;
    private RideHistoryAdapter adapter;
    private List<Ride> rideList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);

        initViews(view);
        setupRecyclerView();
        loadMockData();

        return view;
    }

    private void initViews(View view) {
        rvRides = view.findViewById(R.id.rv_rides);
    }

    private void setupRecyclerView() {
        rideList = new ArrayList<>();
        adapter = new RideHistoryAdapter(rideList);

        rvRides.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRides.setAdapter(adapter);
    }

    // This function will be replaced with real data from backend
    private void loadMockData() {
        rideList.clear();

        // Mock ride data
        rideList.add(new Ride(
                "1",
                "Peter Petrovic",
                "123465798",
                "Kneza Milosa 3",
                "Kajmakalinska 5",
                "24 Oct, 2025",
                1500.00,
                "Not cancelled",
                false,
                4.0f
        ));

        rideList.add(new Ride(
                "2",
                "Marko Markovic",
                "987654321",
                "Despota Stefana 4",
                "Sekspirova 2",
                "24 Oct, 2025",
                2500.00,
                "By passenger",
                false,
                5.0f
        ));

        rideList.add(new Ride(
                "3",
                "Ana Anic",
                "456789123",
                "Kozaciniskog 1",
                "Stalijinova 10",
                "18 Oct, 2025",
                450.00,
                "By driver",
                false,
                4.0f
        ));

        rideList.add(new Ride(
                "4",
                "Jovan Jovanovic",
                "321654987",
                "Meksinijeva 28",
                "Mise Dimitrijevica 32",
                "8 Oct, 2025",
                552.00,
                "By passenger",
                true,
                4.0f
        ));

        rideList.add(new Ride(
                "5",
                "Nikola Nikolic",
                "147258369",
                "Makedonska 14",
                "Gogoljeva 25",
                "15 Sep, 2025",
                1150.00,
                "Not cancelled",
                false,
                3.0f
        ));

        rideList.add(new Ride(
                "6",
                "Stefan Stefanovic",
                "963852741",
                "Masarikova 88",
                "Danila Kisa 11",
                "12 Sep, 2025",
                917.00,
                "Not cancelled",
                false,
                5.0f
        ));

        adapter.notifyDataSetChanged();
    }
}