package com.ognjen.fleetforge;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ognjen.fleetforge.utils.AuthManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AuthManager authManager;

    public DriverProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileDriver.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverProfile newInstance(String param1, String param2) {
        DriverProfile fragment = new DriverProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);
        Button logout=view.findViewById(R.id.btn_logout);
        logout.setOnClickListener(v -> {authManager.logout();

            // Restart MainActivity to show unregistered state
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();});

        Button resetPass=view.findViewById(R.id.resetPassBtn);
        resetPass.setOnClickListener(v -> {
            Fragment passwordChangeProfile = new PasswordChangeProfile();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, passwordChangeProfile)
                    .commit();
        });

        SwitchCompat sw=view.findViewById(R.id.dataShowDriverSwitch);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) { // Vehicle
                view.findViewById(R.id.userSection).setVisibility(View.GONE);
                view.findViewById(R.id.vehicleSection).setVisibility(VISIBLE);
            } else { // User
                view.findViewById(R.id.userSection).setVisibility(VISIBLE);
                view.findViewById(R.id.vehicleSection).setVisibility(GONE);
            }
        });
        authManager = AuthManager.getInstance(requireContext());


        return view;
    }
}