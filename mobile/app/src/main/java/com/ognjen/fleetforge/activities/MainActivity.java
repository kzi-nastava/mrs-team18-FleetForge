package com.ognjen.fleetforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ognjen.fleetforge.fragments.driver.DriverHistoryFragment;
import com.ognjen.fleetforge.fragments.PlaceholderFragment;
import com.ognjen.fleetforge.fragments.common.ProfileFragment;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.unregistered.UnregisteredFragment;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.utils.AuthManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private AuthManager authManager;
    private UserRole currentRole;

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
            case USER:
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

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (currentRole == UserRole.UNREGISTERED && id != R.id.nav_unregistered_home) {
            redirectToLogin();
            return false;
        }

        Fragment fragment = getFragmentForMenuItem(id);
        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }

        return false;
    }

    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.nav_unregistered_home) {
            return new UnregisteredFragment();
        }

        else if (itemId == R.id.nav_profile) {
            return new ProfileFragment();
        }else if (itemId == R.id.nav_home) {
            return PlaceholderFragment.newInstance("Home");
        }else if (itemId == R.id.nav_dashboard) {
            return PlaceholderFragment.newInstance("Dashboard");
        } else if (itemId == R.id.nav_history_user) {
            return PlaceholderFragment.newInstance("Ride History");
        }else if (itemId == R.id.nav_history_driver) {
            return new DriverHistoryFragment();
        }
        else if (itemId == R.id.nav_history_admin) {
            return PlaceholderFragment.newInstance("Ride History");
        }
        else if (itemId == R.id.nav_chat) {
            return PlaceholderFragment.newInstance("Live Chat");
        }
        else if (itemId == R.id.nav_current_ride) {
            return PlaceholderFragment.newInstance("Current Ride");
        }
        else if (itemId == R.id.nav_register_driver) {
            return PlaceholderFragment.newInstance("Register Driver");
        } else if (itemId == R.id.nav_block_users) {
            return PlaceholderFragment.newInstance("Block Users");
        }

        return null;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, MockLoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserRole newRole = authManager.getCurrentRole();
        if (newRole != currentRole) {
            recreate();
        }
    }
}