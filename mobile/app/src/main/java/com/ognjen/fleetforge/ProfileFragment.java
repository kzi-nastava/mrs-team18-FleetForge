package com.ognjen.fleetforge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.utils.AuthManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


public class ProfileFragment extends Fragment {
    // Used for mocking logging in and out, will be changed later
    private AuthManager authManager;
    private TextView tvName, tvEmail, tvRole;
    private MaterialButton btnLogout, btnLogin;
    private MaterialCardView cardProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        authManager = AuthManager.getInstance(requireContext());

        initViews(view);
        setupUI();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvRole = view.findViewById(R.id.tv_profile_role);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogin = view.findViewById(R.id.btn_login);
        cardProfile = view.findViewById(R.id.card_profile);
    }

    private void setupUI() {
        UserRole currentRole = authManager.getCurrentRole();

        if (currentRole == UserRole.UNREGISTERED) {
            // Show login option for unregistered users
            cardProfile.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        } else {
            // Show profile info for logged-in users
            cardProfile.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);

            tvName.setText(authManager.getUserName());
            tvEmail.setText(authManager.getUserEmail());
            tvRole.setText(currentRole.getDisplayName());
        }
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> handleLogout());
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void handleLogout() {
        authManager.logout();

        // Restart MainActivity to show unregistered state
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void handleLogin() {
        // Navigate to login screen
        Intent intent = new Intent(getActivity(), MockLoginActivity.class);
        startActivity(intent);
    }
}