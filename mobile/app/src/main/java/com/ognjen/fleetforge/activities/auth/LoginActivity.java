package com.ognjen.fleetforge.activities.auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.activities.MainActivity;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.LoginRequestDTO;
import com.ognjen.fleetforge.dtos.LoginResponseDTO;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.auth.AuthManager;
import com.ognjen.fleetforge.services.WebSocketService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "FFLOG";

    private EditText etEmail;
    private EditText etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegister;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "LoginActivity: onCreate");

        authManager = AuthManager.getInstance(this);

        // Check if user is already logged in
        if (authManager.isLoggedIn()) {
            Log.d(TAG, "LoginActivity: User already logged in, navigating to MainActivity");
            navigateToMain();
            return;
        }

        initViews();
        setupListeners();
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            android.net.Uri data = intent.getData();
            if (data != null) {
                String token = data.getQueryParameter("token");
                if (token != null) {
                    performActivation(token);
                }
            }
        }
    }

    private void performActivation(String token) {
        // Show a loading dialog if you want
        Log.d(TAG, "Activating account with token: " + token);

        // Call your Retrofit service (ensure you add activateAccount to your API interface)
        RetrofitClient.getInstance().getAuthService().activateAccount(token)
                .enqueue(new Callback<Void>() { // Use Void if response body is empty
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showSuccessDialog("Account Activated", "Your account is now active. You can log in.");
                        } else {
                            Toast.makeText(LoginActivity.this, "Activation failed: Token invalid or expired", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Network error during activation", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuccessDialog(String title, String message) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void initViews() {
        Log.d(TAG, "LoginActivity: Initializing views");
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void setupListeners() {
        Log.d(TAG, "LoginActivity: Setting up listeners");

        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "LoginActivity: Login button clicked");
            performLogin();
        });

        tvForgotPassword.setOnClickListener(v -> {
            Log.d(TAG, "LoginActivity: Forgot password clicked");
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvRegister.setOnClickListener(v -> {
            Log.d(TAG, "LoginActivity: Register clicked");
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "LoginActivity: Attempting login with email: " + email);

        // Validate input
        if (email.isEmpty()) {
            Log.w(TAG, "LoginActivity: Email is empty");
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            Log.w(TAG, "LoginActivity: Password is empty");
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        Log.d(TAG, "LoginActivity: Login button disabled");

        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);

        Log.d(TAG, "LoginActivity: Making API call to login endpoint");
        RetrofitClient.getInstance().getAuthService().login(loginRequest)
                .enqueue(new Callback<LoginResponseDTO>() {
                    @Override
                    public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                        btnLogin.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    requestPermissions(
                                            new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                            1001
                                    );
                                }
                            }
                            LoginResponseDTO loginResponse = response.body();
                            String token = loginResponse.getToken();
                            String role = loginResponse.getRole();
                            String loggedInAt = loginResponse.getLoggedInAt();

                            Log.d(TAG, "LoginActivity: Login successful");
                            Log.d(TAG, "LoginActivity: Received token: " + token);
                            Log.d(TAG, "LoginActivity: User role: " + role);
                            Log.d(TAG, "LoginActivity: Logged in at: " + loggedInAt);

                            Intent serviceIntent = new Intent(LoginActivity.this, WebSocketService.class);
                            serviceIntent.putExtra("TOKEN", token);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(serviceIntent);
                            } else {
                                startService(serviceIntent);
                            }

                            UserRole userRole = mapStringToUserRole(role);
                            authManager.login(userRole, email, email);
                            authManager.saveToken(token);

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            navigateToMain();
                        } else {
                            Log.e(TAG, "LoginActivity: Login failed - Response code: " + response.code());
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "LoginActivity: Error body: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "LoginActivity: Could not read error body", e);
                            }
                            Toast.makeText(LoginActivity.this, "Login failed: Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        Log.e(TAG, "LoginActivity: Network error during login", t);
                        Log.e(TAG, "LoginActivity: Error message: " + t.getMessage());
                        Toast.makeText(LoginActivity.this, "Login failed: Network error - " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private UserRole mapStringToUserRole(String role) {
        Log.d(TAG, "LoginActivity: Mapping role string to UserRole: " + role);

        if (role == null) {
            Log.w(TAG, "LoginActivity: Role is null, defaulting to UNREGISTERED");
            return UserRole.UNREGISTERED;
        }

        // Handle both "ROLE_PASSENGER" and "PASSENGER" formats
        String normalizedRole = role.toUpperCase().replace("ROLE_", "");

        switch (normalizedRole) {
            case "PASSENGER":
                return UserRole.PASSENGER;
            case "DRIVER":
                return UserRole.DRIVER;
            case "ADMIN":
                return UserRole.ADMIN;
            default:
                Log.w(TAG, "LoginActivity: Unknown role: " + role + ", defaulting to UNREGISTERED");
                return UserRole.UNREGISTERED;
        }
    }

    private void navigateToMain() {
        Log.d(TAG, "LoginActivity: Navigating to MainActivity");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LoginActivity: onDestroy");
    }
}