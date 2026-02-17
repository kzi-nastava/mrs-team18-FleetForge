package com.ognjen.fleetforge.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ognjen.fleetforge.R;

public class AdminRidesFragment extends Fragment {

    public AdminRidesFragment() {}

    public static AdminRidesFragment newInstance() {
        return new AdminRidesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_rides, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new RidesPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("History");
                    } else {
                        tab.setText("Active");
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
                return new AdminHistoryFragment();
            } else {
                return new AdminActiveRidesFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}

