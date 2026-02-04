package com.ognjen.fleetforge.fragments.account_changes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.DriverChangesListAdapter;
import com.ognjen.fleetforge.adapters.VehicleChangesListAdapter;
import com.ognjen.fleetforge.databinding.FragmentDriverChangesBinding;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.model.DriverProfileChangeRequest;
import com.ognjen.fleetforge.model.VehicleInformationChangeRequest;

import java.util.ArrayList;


public class DriverChangesFragment extends Fragment {

    private  FragmentDriverChangesBinding binding;
    public static ArrayList<DriverProfileChangeRequest> changes=new ArrayList<>();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private ListView listView;
    private DriverChangesListAdapter driverAdapter;
    private VehicleChangesListAdapter vehicleAdapter;
    private ArrayList<DriverProfileChangeRequest> driverChanges=new ArrayList<>();
    private ArrayList<VehicleInformationChangeRequest> vehicleChanges=new ArrayList<>();

    public DriverChangesFragment() {
        // Required empty public constructor
    }
    public static DriverChangesFragment newInstance(String param1, String param2) {
        DriverChangesFragment fragment = new DriverChangesFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_changes, container, false);

        listView = view.findViewById(R.id.changesList);
        SwitchCompat switchView = view.findViewById(R.id.dataShowDriverSwitch);

        initDriverChanges();
        initVehicleChanges();

        driverAdapter = new DriverChangesListAdapter(getActivity(), driverChanges);
        listView.setAdapter(driverAdapter);

        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (vehicleAdapter == null) {
                    vehicleAdapter = new VehicleChangesListAdapter(getActivity(), vehicleChanges);
                }
                listView.setAdapter(vehicleAdapter);
            } else {
                listView.setAdapter(driverAdapter);
            }
        });

        return view;
    }
    private void initDriverChanges(){
        if(!driverChanges.isEmpty()) {
            driverChanges.clear();
        }
        DriverProfileChangeRequest d1=new DriverProfileChangeRequest(false,"Miroslav","Antic",
                "miroslav@gmail.com","3542354325","adresa1","android.resource://com.ognjen.fleetforge/" + R.drawable.blank_profile__1_,
                "Nikola","Antic","miroslav@gmail.com","3542354325","adresa1","android.resource://com.ognjen.fleetforge/" + R.drawable.blank_profile__1_);
        DriverProfileChangeRequest d2=new DriverProfileChangeRequest(false,"Milica","Petrovic",
                "miroslav@gmail.com","3542354325","adresa1","android.resource://com.ognjen.fleetforge/" + R.drawable.blank_profile__1_,
                "Nikolina","Petrovic","miroslav@gmail.com","3542354325","adresa1","android.resource://com.ognjen.fleetforge/" + R.drawable.blank_profile__1_);

        driverChanges.add(d1);
        driverChanges.add(d2);
    }
    private void initVehicleChanges(){
        if(!vehicleChanges.isEmpty()) {
            vehicleChanges.clear();
        }
        VehicleInformationChangeRequest v1= new VehicleInformationChangeRequest(false,"Petar","Petrovic","Toyota",
                VehicleType.VAN,"VS-233-GG",4,true,false,"Opel",
                VehicleType.VAN,"VS-233-GG",4,true,false);
        VehicleInformationChangeRequest v2= new VehicleInformationChangeRequest(false,"Milica","Milic","Toyota",
                VehicleType.VAN,"VS-233-GG",4,true,false,"Opel",
                VehicleType.VAN,"VS-233-GG",4,true,false);
        vehicleChanges.add(v1);
        vehicleChanges.add(v2);
    }
}