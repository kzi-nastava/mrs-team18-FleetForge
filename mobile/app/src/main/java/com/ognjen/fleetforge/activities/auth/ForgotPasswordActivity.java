package com.ognjen.fleetforge.activities.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.ognjen.fleetforge.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }
}
