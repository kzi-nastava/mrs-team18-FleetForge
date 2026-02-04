package com.ognjen.fleetforge.fragments.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.viewmodels.RegistrationViewModel;

public class RegisterContactFragment extends Fragment {
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_contact, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        TextInputLayout tilEmail = view.findViewById(R.id.til_email);
        TextInputLayout tilPhone = view.findViewById(R.id.til_phone);
        MaterialButton btnNext = view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(v -> {
            viewModel.email = tilEmail.getEditText().getText().toString();
            viewModel.phone = tilPhone.getEditText().getText().toString();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.register_container, new RegisterSecurityFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}