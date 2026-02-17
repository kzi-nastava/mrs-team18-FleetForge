package com.ognjen.fleetforge.fragments.passenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.PlaceholderFragment;

public class PassengerRidesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public PassengerRidesFragment() {}

    public static PassengerRidesFragment newInstance() {
        return new PassengerRidesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_passenger_rides, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new RidesPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("History");
                    } else {
                        tab.setText("Scheduled");
                    }
                }).attach();

        return view;
    }

    private static class RidesPagerAdapter extends FragmentStateAdapter {

        public RidesPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new PassengerHistoryFragment();
            } else {
                return new ScheduledRidesFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
