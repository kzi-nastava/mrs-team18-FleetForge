package com.ognjen.fleetforge.fragments.passenger;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.fragments.common.PasswordChangeProfile;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.utils.AuthManager;
import com.ognjen.fleetforge.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PassengerProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PassengerProfile extends Fragment {

    private static final String BaseUrl="http://"+ BuildConfig.IP_ADDR+":8080";
    private AuthManager authManager;
    private PassengerProfileViewModel passengerProfileViewModel;
    private ShapeableImageView profilePic;
    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText phoneNumber;
    private TextInputEditText address;

    public PassengerProfile() {
        // Required empty public constructor
    }

    public static PassengerProfile newInstance(String param1, String param2) {
        PassengerProfile fragment = new PassengerProfile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_profile, container, false);
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
        authManager = AuthManager.getInstance(requireContext());

        profilePic=view.findViewById(R.id.profileImage);
        firstName=view.findViewById(R.id.firstName);
        lastName=view.findViewById(R.id.lastName);
        email=view.findViewById(R.id.email);
        phoneNumber=view.findViewById(R.id.phoneNumber);
        address=view.findViewById(R.id.address);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passengerProfileViewModel=new ViewModelProvider(this).get(PassengerProfileViewModel.class);
        passengerProfileViewModel.getPassengerProfile().observe(getViewLifecycleOwner(),passenger->{
            if(passenger!=null){
                String imgUrl=BaseUrl+passenger.getProfilePicture();
                Glide.with(requireContext())
                        .load(imgUrl)
                        .into(profilePic);
                firstName.setText(passenger.getFirstName());
                lastName.setText(passenger.getLastName());
                email.setText(passenger.getEmail());
                phoneNumber.setText(passenger.getPhoneNumber());
                address.setText(passenger.getAddress());
            }
        });

    }
}