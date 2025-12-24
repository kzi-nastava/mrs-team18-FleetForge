package com.ognjen.fleetforge.fragments.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.ognjen.fleetforge.R;

public class RegisterSecurityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_security, container, false);

        MaterialButton btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.register_container, new RegisterProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
