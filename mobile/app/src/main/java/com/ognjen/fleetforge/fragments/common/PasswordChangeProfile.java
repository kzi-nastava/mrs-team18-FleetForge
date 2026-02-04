package com.ognjen.fleetforge.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.admin.AdminProfile;
import com.ognjen.fleetforge.fragments.admin.DriverChangesViewModel;
import com.ognjen.fleetforge.fragments.driver.DriverProfile;
import com.ognjen.fleetforge.fragments.passenger.PassengerProfile;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.utils.AuthManager;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordChangeProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordChangeProfile extends Fragment {

    private PasswordChangeViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AuthManager authManager;
    public PasswordChangeProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordChangeProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordChangeProfile newInstance(String param1, String param2) {
        PasswordChangeProfile fragment = new PasswordChangeProfile();
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
        viewModel=new ViewModelProvider(this).get(PasswordChangeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_password_change_profile,container,false);
       Button cancel= view.findViewById(R.id.cancelBtn);
       authManager=AuthManager.getInstance(requireActivity());
       cancel.setOnClickListener(v -> {
//           requireActivity().getSupportFragmentManager().popBackStack();
           Fragment fr = null;
           if(authManager.getCurrentRole()== UserRole.DRIVER){
               fr=new DriverProfile();
           }else if(authManager.getCurrentRole()==UserRole.ADMIN){
               //todo
           }
           else{
               fr=new PassengerProfile();
           }
           requireActivity().getSupportFragmentManager()
                   .beginTransaction()
                   .replace(R.id.fragment_container, fr)
                   .commit();
       });
       TextInputEditText newPassword= view.findViewById(R.id.newPassword);
       TextInputEditText confirmPassword=view.findViewById(R.id.confirmNewPassword);

       Button reset= view.findViewById(R.id.changeBtn);
       reset.setOnClickListener(v -> {
           if(!Objects.requireNonNull(newPassword.getText()).toString().equals(Objects.requireNonNull(confirmPassword.getText()).toString())||newPassword.getText().toString().length()<8){
               Toast.makeText(getContext(),"Passwords do not match or password is too short try again",Toast.LENGTH_SHORT).show();
           }else {
               viewModel.changePassword(newPassword.getText().toString(),authManager.getCurrentRole()).observe(getViewLifecycleOwner(),response->{
                   if(response==true){
                       Toast.makeText(getContext(),"Passwords changed",Toast.LENGTH_SHORT).show();
                       Fragment fr = null;
                       if(authManager.getCurrentRole()== UserRole.DRIVER){
                           fr=new DriverProfile();
                       }else if(authManager.getCurrentRole()==UserRole.ADMIN){
                           fr=new AdminProfile();
                       }
                       else{
                           fr=new PassengerProfile();
                       }
                       requireActivity().getSupportFragmentManager()
                               .beginTransaction()
                               .replace(R.id.fragment_container, fr)
                               .commit();

                   }else{
                       Toast.makeText(getContext(),"Passwords was not changed, error",Toast.LENGTH_SHORT).show();
                   }
               });
           }
       });
       return view;
    }
}