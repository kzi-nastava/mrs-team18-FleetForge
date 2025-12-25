package com.ognjen.fleetforge.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.fragments.driver.DriverProfile;
import com.ognjen.fleetforge.fragments.passenger.PassengerProfile;
import com.ognjen.fleetforge.model.UserRole;
import com.ognjen.fleetforge.utils.AuthManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordChangeProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordChangeProfile extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_password_change_profile,container,false);
       Button cancel= view.findViewById(R.id.cancelBtn);
       authManager=AuthManager.getInstance(requireActivity());
       cancel.setOnClickListener(v -> {
//           requireActivity().getSupportFragmentManager().popBackStack();
           Fragment fr;
           if(authManager.getCurrentRole()== UserRole.DRIVER){
               fr=new DriverProfile();
           }
           else{
               fr=new PassengerProfile();
           }
           requireActivity().getSupportFragmentManager()
                   .beginTransaction()
                   .replace(R.id.fragment_container, fr)
                   .commit();
       });
       return view;
    }
}