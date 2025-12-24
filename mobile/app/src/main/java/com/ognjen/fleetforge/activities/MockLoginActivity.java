package com.ognjen.fleetforge.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.utils.AuthManager;
import com.google.android.material.button.MaterialButton;


public class MockLoginActivity extends AppCompatActivity {

    private MaterialButton btnLoginUser;
    private MaterialButton btnLoginDriver;
    private MaterialButton btnLoginAdmin;
    private MaterialButton btnContinueGuest;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_login);

        authManager = AuthManager.getInstance(this);

        initViews();

        setupListeners();
    }

    private void initViews() {
        btnLoginUser = findViewById(R.id.btn_login_user);
        btnLoginDriver = findViewById(R.id.btn_login_driver);
        btnLoginAdmin = findViewById(R.id.btn_login_admin);
        btnContinueGuest = findViewById(R.id.btn_continue_guest);
    }

    private void setupListeners() {
        // Login as USER
        btnLoginUser.setOnClickListener(v -> {
            authManager.login(
                    UserRole.USER,
                    "John Doe",
                    "john.doe@example.com"
            );
            navigateToMain();
        });

        btnLoginDriver.setOnClickListener(v -> {
            authManager.login(
                    UserRole.DRIVER,
                    "Mike Driver",
                    "mike.driver@example.com"
            );
            navigateToMain();
        });

        btnLoginAdmin.setOnClickListener(v -> {
            authManager.login(
                    UserRole.ADMIN,
                    "Admin User",
                    "admin@example.com"
            );
            navigateToMain();
        });

        btnContinueGuest.setOnClickListener(v -> {
            authManager.logout();
            navigateToMain();
        });
    }


    private void navigateToMain() {
        Intent intent = new Intent(MockLoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}