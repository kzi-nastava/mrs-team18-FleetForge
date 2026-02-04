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

public class RegisterSecurityFragment extends Fragment {
    private RegistrationViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_security, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        TextInputLayout tilPass = view.findViewById(R.id.til_password);
        TextInputLayout tilConfirm = view.findViewById(R.id.til_confirm_password);
        MaterialButton btnNext = view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(v -> {
            String pass = tilPass.getEditText().getText().toString();
            String confirm = tilConfirm.getEditText().getText().toString();

            if (!pass.equals(confirm)) {
                tilConfirm.setError("Passwords do not match");
                return;
            }

            viewModel.password = pass;
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.register_container, new RegisterProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
