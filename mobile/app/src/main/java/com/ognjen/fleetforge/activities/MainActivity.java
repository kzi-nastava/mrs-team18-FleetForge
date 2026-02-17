package com.ognjen.fleetforge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.ognjen.fleetforge.fragments.admin.AdminChatFragment;
import com.ognjen.fleetforge.fragments.admin.AdminHistoryFragment;
import com.ognjen.fleetforge.fragments.admin.AdminProfile;
import com.ognjen.fleetforge.fragments.admin.DriverChangesFragment;
import com.ognjen.fleetforge.activities.auth.LoginActivity;
import com.ognjen.fleetforge.fragments.admin.UsersList;
import com.ognjen.fleetforge.fragments.driver.CurrentRideDriver;
import com.ognjen.fleetforge.fragments.admin.RegisterDriver;
import com.ognjen.fleetforge.fragments.driver.DriverHistoryFragment;
import com.ognjen.fleetforge.fragments.PlaceholderFragment;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.passenger.CurrentRidePassenger;
import com.ognjen.fleetforge.fragments.passenger.FavoriteRoutes;
import com.ognjen.fleetforge.fragments.passenger.PassengerChatFragment;
import com.ognjen.fleetforge.fragments.passenger.PassengerHistoryFragment;
import com.ognjen.fleetforge.fragments.passenger.PassengerRidesFragment;
import com.ognjen.fleetforge.fragments.passenger.RideOrder;
import com.ognjen.fleetforge.fragments.unregistered.UnregisteredFragment;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.auth.AuthManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ognjen.fleetforge.fragments.driver.DriverProfile;
import com.ognjen.fleetforge.fragments.passenger.PassengerProfile;

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

        getWindow().setDecorFitsSystemWindows(false);
        View mainView = findViewById(R.id.bottom_navigation).getRootView();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });
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

    private Fragment createFragmentForMenuItem(int itemId) {
        if (itemId == R.id.nav_unregistered_home) {
            return new UnregisteredFragment();
        } else if (itemId == R.id.nav_profile) {
            if (currentRole == UserRole.DRIVER) {
                return new DriverProfile();
            }else if(currentRole==UserRole.PASSENGER){
                return new PassengerProfile();
            }else{
                return new AdminProfile();
            }
        } else if (itemId == R.id.nav_home) {
            return new RideOrder();
        } else if (itemId == R.id.nav_dashboard) {
            return PlaceholderFragment.newInstance("Dashboard");
        } else if (itemId == R.id.nav_history_user) {
            return new PassengerRidesFragment();
        } else if (itemId == R.id.nav_history_driver) {
            return new DriverHistoryFragment();
        }else if (itemId== R.id.nav_current_driver){
            return new CurrentRideDriver();
        } else if (itemId == R.id.nav_history_admin) {
            return new AdminHistoryFragment();
        } else if (itemId == R.id.nav_chat_admin) {
            return new AdminChatFragment();
        } else if (itemId == R.id.nav_chat_driver) {
            return new PassengerChatFragment();
        } else if (itemId == R.id.nav_chat_passenger) {
            return new PassengerChatFragment();
        } else if (itemId == R.id.nav_current_ride) {
            return new CurrentRidePassenger();
        } else if (itemId == R.id.nav_register_driver) {
            return new RegisterDriver();
        } else if (itemId == R.id.nav_block_users) {
            return new UsersList();
        } else if (itemId == R.id.nav_profile_changes) {
            return new DriverChangesFragment();
        }else if(itemId==R.id.nav_favorites){
            return new FavoriteRoutes();
        }
        return null;
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (currentRole == UserRole.UNREGISTERED && id != R.id.nav_unregistered_home) {
            redirectToLogin();
            return false;
        }

        Fragment fragment = createFragmentForMenuItem(id);
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
            recreate();
        }
    }

}

