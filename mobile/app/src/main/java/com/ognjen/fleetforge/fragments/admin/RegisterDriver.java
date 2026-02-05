package com.ognjen.fleetforge.fragments.admin;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;

import java.io.IOException;


public class RegisterDriver extends Fragment {
    RegisterDriverViewModel viewModel;
    private static final String BaseUrl="http://"+ BuildConfig.IP_ADDR+":8080";
    private ShapeableImageView profilePic;
    private ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            profilePic.setImageURI(uri);

                        }
                    });
    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText phoneNumber;
    private TextInputEditText address;
    private TextInputEditText model;
    private MaterialAutoCompleteTextView vehicleType;

    private TextInputEditText licencePlate;
    private TextInputEditText passengers;
    private MaterialCheckBox babySeat;
    private MaterialCheckBox petFriendly;

    private Button next;
    private Button back;
    private Button create;


    public RegisterDriver() {
        // Required empty public constructor
    }


    public static RegisterDriver newInstance(String param1, String param2) {
        RegisterDriver fragment = new RegisterDriver();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= new ViewModelProvider(this).get(RegisterDriverViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_register_driver, container, false);
        profilePic=view.findViewById(R.id.profileImage);
        profilePic.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });
        next=view.findViewById(R.id.nextBtn);
        next.setOnClickListener(v -> {
            if(checkFirstPartValid()&&isEmailValid(email)) {
                view.findViewById(R.id.driverPart).setVisibility(GONE);
                view.findViewById(R.id.vehiclePart).setVisibility(VISIBLE);
            }else{
                Toast.makeText(getContext(),"All fields must be filled or email is not in valid format.",Toast.LENGTH_SHORT).show();
            }
        });

        back=view.findViewById(R.id.backBtn);
        back.setOnClickListener(v -> {
            view.findViewById(R.id.driverPart).setVisibility(VISIBLE);
            view.findViewById(R.id.vehiclePart).setVisibility(GONE);
        });

        firstName=view.findViewById(R.id.firstName);
        lastName=view.findViewById(R.id.lastName);
        email=view.findViewById(R.id.email);
        phoneNumber=view.findViewById(R.id.phoneNumber);
        address=view.findViewById(R.id.address);

        model=view.findViewById(R.id.model);
        vehicleType=view.findViewById(R.id.vehicleTypeSpinner);
        licencePlate=view.findViewById(R.id.registration);
        passengers=view.findViewById(R.id.passengers);
        babySeat=view.findViewById(R.id.babySeatCB);
        petFriendly=view.findViewById(R.id.petFriendlyCB);

        ArrayAdapter<String> adapter=  new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.vehicle_types)
        );
        vehicleType.setAdapter(adapter);

       return view;
    }

    private boolean checkFirstPartValid(){
        if(!firstName.getText().toString().isEmpty()
        &&!lastName.getText().toString().isEmpty()
        &&!email.getText().toString().isEmpty()
        &&!phoneNumber.getText().toString().isEmpty()
        &&!address.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }
    private boolean isEmailValid(TextInputEditText email) {
        String emailText = email.getText().toString().trim();
        return !emailText.isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
    }
}