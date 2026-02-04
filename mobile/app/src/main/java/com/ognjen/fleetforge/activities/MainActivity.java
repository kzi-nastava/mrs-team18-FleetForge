package com.ognjen.fleetforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ognjen.fleetforge.activities.auth.LoginActivity;
import com.ognjen.fleetforge.fragments.account_changes.DriverChangesFragment;
import com.ognjen.fleetforge.fragments.driver.DriverHistoryFragment;
import com.ognjen.fleetforge.fragments.PlaceholderFragment;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.unregistered.UnregisteredFragment;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.auth.AuthManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ognjen.fleetforge.fragments.driver.DriverProfile;
import com.ognjen.fleetforge.fragments.passenger.PassengerProfile;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private AuthManager authManager;
    private UserRole currentRole;

    private final Map<Integer, Fragment> fragmentCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = AuthManager.getInstance(this);
        currentRole = authManager.getCurrentRole();

        bottomNavigation = findViewById(R.id.bottom_navigation);

        setupBottomNavigation();
        loadInitialFragment();
    }

    private void setupBottomNavigation() {
        switch (currentRole) {
            case UNREGISTERED:
                bottomNavigation.getMenu().clear();
                bottomNavigation.inflateMenu(R.menu.bottom_nav_unregistered);
                bottomNavigation.setSelectedItemId(R.id.nav_unregistered_home);
                break;
            case PASSENGER:
                bottomNavigation.getMenu().clear();
                bottomNavigation.inflateMenu(R.menu.bottom_nav_user);
                break;
            case DRIVER:
                bottomNavigation.getMenu().clear();
                bottomNavigation.inflateMenu(R.menu.bottom_nav_driver);
                break;
            case ADMIN:
                bottomNavigation.getMenu().clear();
                bottomNavigation.inflateMenu(R.menu.bottom_nav_admin);
                break;
        }

        bottomNavigation.setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private void loadInitialFragment() {
        Fragment initialFragment;

        if (currentRole == UserRole.UNREGISTERED) {
            initialFragment = new UnregisteredFragment();
        } else {
            initialFragment = PlaceholderFragment.newInstance("Dashboard");
        }

        loadFragment(initialFragment);
    }

    private Fragment getOrCreateFragment(int itemId) {
        if (fragmentCache.containsKey(itemId)) {
            return fragmentCache.get(itemId);
        }
        Fragment fragment = createFragmentForMenuItem(itemId);
        if (fragment != null) {
            fragmentCache.put(itemId, fragment);
        }
        return fragment;
    }

    private Fragment createFragmentForMenuItem(int itemId) {
        if (itemId == R.id.nav_unregistered_home) {
            return new UnregisteredFragment();
        } else if (itemId == R.id.nav_profile) {
            if (currentRole == UserRole.DRIVER) {
                return new DriverProfile();
            } else {
                return new PassengerProfile();
            }
        } else if (itemId == R.id.nav_home) {
            return PlaceholderFragment.newInstance("Home");
        } else if (itemId == R.id.nav_dashboard) {
            return PlaceholderFragment.newInstance("Dashboard");
        } else if (itemId == R.id.nav_history_user) {
            return PlaceholderFragment.newInstance("Ride History");
        } else if (itemId == R.id.nav_history_driver) {
            return new DriverHistoryFragment();
        } else if (itemId == R.id.nav_history_admin) {
            return PlaceholderFragment.newInstance("Ride History");
        } else if (itemId == R.id.nav_chat) {
            return PlaceholderFragment.newInstance("Live Chat");
        } else if (itemId == R.id.nav_current_ride) {
            return PlaceholderFragment.newInstance("Current Ride");
        } else if (itemId == R.id.nav_register_driver) {
            return PlaceholderFragment.newInstance("Register Driver");
        } else if (itemId == R.id.nav_block_users) {
            return PlaceholderFragment.newInstance("Block Users");
        } else if (itemId == R.id.nav_profile_changes) {
            return new DriverChangesFragment();
        }
        return null;
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (currentRole == UserRole.UNREGISTERED && id != R.id.nav_unregistered_home) {
            redirectToLogin();
            return false;
        }

        Fragment fragment = getOrCreateFragment(id);
        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserRole newRole = authManager.getCurrentRole();
        if (newRole != currentRole) {
            fragmentCache.clear();
            recreate();
        }
    }

}