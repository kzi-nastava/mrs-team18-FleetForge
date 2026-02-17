package com.ognjen.fleetforge.fragments.passenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.fragments.common.PasswordChangeProfile;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.auth.AuthManager;
import com.ognjen.fleetforge.activities.MainActivity;
import com.ognjen.fleetforge.fragments.reports.UserReports;
import com.ognjen.fleetforge.services.WebSocketService;
import com.ognjen.fleetforge.viewmodels.PassengerProfileViewModel;

import java.io.IOException;

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
    private Button changeBtn;
    private ActivityResultLauncher<String> imagePicker;

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
        passengerProfileViewModel=new ViewModelProvider(this).get(PassengerProfileViewModel.class);
        imagePicker =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                passengerProfileViewModel.setSelectedImageUri(uri);
                                profilePic.setImageURI(uri);
                            }
                        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_profile, container, false);
        Button logout=view.findViewById(R.id.btn_logout);
        logout.setOnClickListener(v -> {authManager.logout();

            Intent serviceIntent = new Intent(requireContext(), WebSocketService.class);
            requireActivity().stopService(serviceIntent);
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
        changeBtn=view.findViewById(R.id.changeBtn);

        profilePic.setOnClickListener(v->{
            imagePicker.launch("image/*");
        });

        Button reports= view.findViewById(R.id.reportsBtn);
        reports.setOnClickListener(v -> {
            Fragment passengerReports = new UserReports();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, passengerReports)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        passengerProfileViewModel.getSelectedImageUri()
                .observe(getViewLifecycleOwner(), uri -> {
                    if (uri != null) {
                        profilePic.setImageURI(uri);
                    }
                });
        changeBtn.setOnClickListener(v -> {
            Uri uri=passengerProfileViewModel.getSelectedImageUri().getValue();
            if(uri!=null) {
                try {
                    passengerProfileViewModel.uploadProfilePicture(getContext(), passengerProfileViewModel.getSelectedImageUri().getValue()).observe(
                            getViewLifecycleOwner(),response->{
                                if(response==true){
                                    passengerProfileViewModel.changeCurrentPassenger(firstName.getText().toString()
                                    ,lastName.getText().toString(), email.getText().toString(),phoneNumber.getText().toString(),
                                            address.getText().toString()).observe(getViewLifecycleOwner(),response2->{
                                        if(response2!=null) {
                                            firstName.setText(response2.getFirstName());
                                            lastName.setText(response2.getLastName());
                                            address.setText(response2.getAddress());
                                            phoneNumber.setText(response2.getPhoneNumber());
                                            email.setText(response2.getEmail());
                                            String imgUrl = BaseUrl + response2.getProfilePicture();
                                            Glide.with(requireContext())
                                                    .load(imgUrl)
                                                    .into(profilePic);
                                            Toast.makeText(getContext(),"Information changed!", Toast.LENGTH_SHORT).show();
                                        }

                                    });
                                }
                            }
                    );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
}