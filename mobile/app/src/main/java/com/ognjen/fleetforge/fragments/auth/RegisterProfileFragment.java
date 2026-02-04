package com.ognjen.fleetforge.fragments.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.activities.auth.LoginActivity;
import com.ognjen.fleetforge.viewmodels.RegistrationViewModel;

public class RegisterProfileFragment extends Fragment {
    private static final String TAG = "FFLOG";
    private RegistrationViewModel viewModel;
    private ImageView ivProfile;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    ivProfile.setImageURI(uri);
                    viewModel.profileImageUri = uri;
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_profile, container, false);
        Log.d(TAG, "RegisterProfileFragment: onCreateView");
        viewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);

        ivProfile = view.findViewById(R.id.iv_profile);
        View imageContainer = view.findViewById(R.id.profile_image_container);

        imageContainer.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        TextInputLayout tilFirst = view.findViewById(R.id.til_first_name);
        TextInputLayout tilLast = view.findViewById(R.id.til_last_name);
        TextInputLayout tilAddr = view.findViewById(R.id.til_address);
        MaterialButton btnRegister = view.findViewById(R.id.btn_register);

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            Log.d(TAG, "RegisterProfileFragment: Register Button Clicked");
            viewModel.firstName = tilFirst.getEditText().getText().toString();
            viewModel.lastName = tilLast.getEditText().getText().toString();
            viewModel.address = tilAddr.getEditText().getText().toString();

            viewModel.registerUser();
        });

        return view;
    }
}