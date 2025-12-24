package com.ognjen.fleetforge.activities.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.auth.RegisterContactFragment;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.register_container, new RegisterContactFragment())
                    .commit();
        }
    }
}
