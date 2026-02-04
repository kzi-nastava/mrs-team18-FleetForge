package com.ognjen.fleetforge.fragments.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.adapters.DriverChangesListAdapter;
import com.ognjen.fleetforge.adapters.VehicleChangesListAdapter;
import com.ognjen.fleetforge.databinding.FragmentDriverChangesBinding;
import com.ognjen.fleetforge.dtos.admin.AdminViewDriverChangesResponseDTO;
import com.ognjen.fleetforge.dtos.admin.AdminViewVehicleChangesResponseDTO;
import com.ognjen.fleetforge.enums.VehicleType;
import com.ognjen.fleetforge.fragments.driver.DriverProfileViewModel;
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

    private DriverChangesViewModel viewModel;

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
        viewModel=new ViewModelProvider(this).get(DriverChangesViewModel.class);
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
        vehicleAdapter = new VehicleChangesListAdapter(getActivity(), vehicleChanges);
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
        driverAdapter.setOnActionListener(new DriverChangesListAdapter.OnActionListener() {
            @Override
            public void onAccept(DriverProfileChangeRequest request, int position) {
                handleDriverAccept(request, position);
            }

            @Override
            public void onReject(DriverProfileChangeRequest request, int position) {
                handleDriverReject(request, position);
            }
        });

        vehicleAdapter.setOnActionListener(new VehicleChangesListAdapter.OnActionListener() {
            @Override
            public void onAccept(VehicleInformationChangeRequest request, int position) {
                handleVehicleAccept(request,position);
            }

            @Override
            public void onReject(VehicleInformationChangeRequest request, int position) {
                handleVehicleReject(request,position);
            }
        });
        return view;
    }
    private void handleDriverAccept(DriverProfileChangeRequest request, int position) {
        viewModel.driverInfoChange(true,request.getRequestId()).observe(getViewLifecycleOwner(),response->{
            Toast.makeText(getContext(),response.getStatus()+" "+response.getId(),Toast.LENGTH_SHORT).show();
        });
        driverChanges.remove(position);
        driverAdapter.notifyDataSetChanged();
    }
    private void handleDriverReject(DriverProfileChangeRequest request, int position) {

        viewModel.vehicleInfoChange(false,request.getRequestId()).observe(getViewLifecycleOwner(),response->{
            Toast.makeText(getContext(),response.getStatus()+" "+response.getId(),Toast.LENGTH_SHORT).show();
        });
        driverChanges.remove(position);
        driverAdapter.notifyDataSetChanged();
    }
    private void handleVehicleAccept(VehicleInformationChangeRequest request,int position){
        viewModel.vehicleInfoChange(true, request.getRequestId()).observe(getViewLifecycleOwner(),response->{
            Toast.makeText(getContext(),response.getStatus()+" "+response.getId(),Toast.LENGTH_SHORT).show();
        });
        vehicleChanges.remove(position);
        vehicleAdapter.notifyDataSetChanged();
    }
    private void handleVehicleReject(VehicleInformationChangeRequest request, int position){
        viewModel.vehicleInfoChange(false,request.getRequestId()).observe(getViewLifecycleOwner(),response->{
            Toast.makeText(getContext(),response.getStatus()+" "+response.getId(),Toast.LENGTH_SHORT).show();
        });
        vehicleChanges.remove(position);
        vehicleAdapter.notifyDataSetChanged();
    }
    private void initDriverChanges(){

        viewModel.getDriversChanges().observe(getViewLifecycleOwner(),responses->{
            if(responses!=null){
                driverChanges.clear();
                for(AdminViewDriverChangesResponseDTO response: responses) {
                    DriverProfileChangeRequest changes = new DriverProfileChangeRequest();
                    changes.setExpanded(false);
                    changes.setRequestId(response.getRequestId());
                    changes.setDriverId(response.getDriverId());
                    changes.setOldFirstName(response.getFirstName());
                    changes.setOldLastName(response.getLastName());
                    changes.setOldEmail(response.getEmail());
                    changes.setOldPhoneNumber(response.getPhoneNumber());
                    changes.setOldAddress(response.getAddress());

                    changes.setNewFirstName(response.getNewFirstName());
                    changes.setNewLastName(response.getNewLastName());
                    changes.setNewAddress(response.getNewAddress());
                    changes.setNewPhoneNumber(response.getNewPhoneNumber());
                    changes.setNewEmail(response.getNewEmail());

                    driverChanges.add(changes);
                }
                driverAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initVehicleChanges(){
        viewModel.getVehiclesChanges().observe(getViewLifecycleOwner(),responses->{
            if(responses!=null){
                vehicleChanges.clear();
                for(AdminViewVehicleChangesResponseDTO response: responses){
                    VehicleInformationChangeRequest changes= new VehicleInformationChangeRequest();
                    changes.setExpanded(false);
                    changes.setFirstName(response.getFirstName());
                    changes.setLastName(response.getLastName());
                    changes.setRequestId(response.getRequestId());
                    changes.setVehicleId(response.getVehicleId());
                    changes.setOldModel(response.getModel());
                    changes.setOldType(response.getType());
                    changes.setOldRegistrationNumber(response.getRegistrationNumber());
                    changes.setOldSpace(response.getSpace());
                    changes.setOldBabySeat(response.isBabySeat());
                    changes.setOldPetFriendly(response.isPetFriendly());

                    changes.setNewModel(response.getNewModel());
                    changes.setNewType(response.getNewType());
                    changes.setNewRegistrationNumber(response.getNewRegistrationNumber());
                    changes.setNewSpace(response.getNewSpace());
                    changes.setNewBabySeat(response.isNewBabySeat());
                    changes.setNewPetFriendly(response.isNewPetFriendly());
                    vehicleChanges.add(changes);
                }
                vehicleAdapter.notifyDataSetChanged();
            }
        });
    }
}