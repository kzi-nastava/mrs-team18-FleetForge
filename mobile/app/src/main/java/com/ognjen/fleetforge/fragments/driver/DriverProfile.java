package com.ognjen.fleetforge.fragments.driver;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.fragments.common.PasswordChangeProfile;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.auth.AuthManager;
import com.ognjen.fleetforge.activities.MainActivity;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverProfile extends Fragment {

    private static final String BaseUrl="http://"+ BuildConfig.IP_ADDR+":8080";
    private String mParam1;
    private String mParam2;
    private DriverProfileViewModel driverProfileViewModel;
    private AuthManager authManager;
    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText phoneNumber;
    private TextInputEditText address;
    private ShapeableImageView profilePic;
    private ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            profilePic.setImageURI(uri);
                            try {
                                driverProfileViewModel.uploadProfilePicture(getContext(),uri).observe(getViewLifecycleOwner(),response->{
                                    if(response.booleanValue()==true){
                                        Toast.makeText(getContext(), "Picture uploaded",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getContext(), "Picture was not uploaded",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
    private TextInputEditText model;
    private TextInputEditText registration;
    private MaterialAutoCompleteTextView dropDown;
    private TextInputEditText passengers;
    private MaterialCheckBox babySeat;
    private MaterialCheckBox petFriendly;

    private Button changeDriver;
    private Button changeVehicle;

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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverProfileViewModel=new ViewModelProvider(this).get(DriverProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);
        dropDown=view.findViewById(R.id.vehicleTypeSpinner);
        ArrayAdapter<String> adapter=  new ArrayAdapter<>(
            getContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.vehicle_types)
        );
        dropDown.setAdapter(adapter);

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

        firstName=view.findViewById(R.id.firstName);
        lastName= view.findViewById(R.id.lastName);
        email=view.findViewById(R.id.email);
        phoneNumber=view.findViewById(R.id.phoneNumber);
        address=view.findViewById(R.id.address);
        profilePic=view.findViewById(R.id.profileImage);
        profilePic.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });

        model=view.findViewById(R.id.model);
        registration=view.findViewById(R.id.registration);
        passengers=view.findViewById(R.id.passengers);
        babySeat=view.findViewById(R.id.babySeatCB);
        petFriendly=view.findViewById(R.id.petFriendlyCB);

        driverProfileViewModel.getLoggedDriver().observe(getViewLifecycleOwner(),driverGetResponseDTO -> {
            if(driverGetResponseDTO!=null){
                firstName.setText(driverGetResponseDTO.getFirstName());
                String imgUrl=BaseUrl+driverGetResponseDTO.getProfilePicture();
                Glide.with(requireContext())
                        .load(imgUrl)
                        .into(profilePic);
                lastName.setText(driverGetResponseDTO.getLastName());
                email.setText(driverGetResponseDTO.getEmail());
                phoneNumber.setText(driverGetResponseDTO.getPhoneNumber());
                address.setText(driverGetResponseDTO.getAddress());

                model.setText(driverGetResponseDTO.getVehicle().getModel());
                String dropDownChoice=driverGetResponseDTO.getVehicle().getType().toString();
                dropDownChoice=dropDownChoice.substring(0,1).toUpperCase()+dropDownChoice.substring(1).toLowerCase();
                dropDown.setText(dropDownChoice,false);
                registration.setText(driverGetResponseDTO.getVehicle().getRegistrationNumber());
                passengers.setText(String.valueOf(driverGetResponseDTO.getVehicle().getSpace()));
                babySeat.setChecked(driverGetResponseDTO.getVehicle().isBabySeat());
                petFriendly.setChecked(driverGetResponseDTO.getVehicle().isPetFriendly());
            }
        });

        changeDriver= view.findViewById(R.id.changeBtn);
        changeVehicle=view.findViewById(R.id.changeVehicleBtn);

        changeDriver.setOnClickListener(v -> {
            driverProfileViewModel.createDriverChangeRequest(firstName.getText().toString()
            ,lastName.getText().toString(),email.getText().toString(), phoneNumber.getText().toString(), address.getText().toString()).observe(getViewLifecycleOwner(),response->{
                if(response!=null){
                    Toast.makeText(getContext(), "Request created. Status: "+response.getStatus()+" Time: "+response.getCreatedAt()
                    ,Toast.LENGTH_SHORT).show();
                }
            });
        });
        changeVehicle.setOnClickListener(v->{
            driverProfileViewModel.createVehicleChangeRequest(model.getText().toString(), VehicleType.valueOf(dropDown.getText().toString().toUpperCase())
            ,registration.getText().toString(),Integer.valueOf(passengers.getText().toString()),petFriendly.isChecked(),babySeat.isChecked()).observe(getViewLifecycleOwner(),response->{
                if(response!=null){
                    Toast.makeText(getContext(), "Request created. Status: "+response.getStatus()+" Time: "+response.getCreatedAt()
                            ,Toast.LENGTH_SHORT).show();
                }
            });
        });
        return view;
    }

}