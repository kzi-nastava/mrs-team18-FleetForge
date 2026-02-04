package com.ognjen.fleetforge.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.AuthService;
import com.ognjen.fleetforge.api.RetrofitClient; // Assuming you have this
import com.ognjen.fleetforge.dtos.PasswordResetRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton btnSend;
    private TextInputEditText etEmail;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authService = RetrofitClient.getInstance().getAuthService();

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnSend = findViewById(R.id.btn_send);
        etEmail = findViewById(R.id.et_email);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                sendResetLink(email);
            } else {
                etEmail.setError("Email is required");
            }
        });
    }

    private void sendResetLink(String email) {
        btnSend.setEnabled(false);

        PasswordResetRequestDTO request = new PasswordResetRequestDTO(email);

        authService.requestPasswordReset(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Reset link has been sent to your email.",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    btnSend.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Server error, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}