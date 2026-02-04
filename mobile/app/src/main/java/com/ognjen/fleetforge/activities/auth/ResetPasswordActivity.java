package com.ognjen.fleetforge.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.AuthService;
import com.ognjen.fleetforge.api.RetrofitClient;
import com.ognjen.fleetforge.dtos.ResetPasswordRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private MaterialButton btnConfirm;
    private AuthService authService;
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        authService = RetrofitClient.getInstance().getLoginService();

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            android.net.Uri data = intent.getData();
            if (data != null) {
                token = data.getQueryParameter("token");
            }
        }

        initViews();
        setupListeners();

        if (token == null) {
            Toast.makeText(this, "Invalid reset link", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (newPass.length() < 6) {
                etNewPassword.setError("Password too short");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            performReset(newPass);
        });
    }

    private void performReset(String password) {
        ResetPasswordRequestDTO body = new ResetPasswordRequestDTO(token, password);

        authService.resetPassword(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Reset failed. Link may be expired.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}