package com.ognjen.fleetforge;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

public class RegisterContactFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_contact, container, false);

        MaterialButton btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.register_container, new RegisterSecurityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
