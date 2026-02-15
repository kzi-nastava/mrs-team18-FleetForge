package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.user.UserInformationDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UsersListAdatper extends ArrayAdapter<UserInformationDTO> {

    private ArrayList<UserInformationDTO> users;
    private Set<Long> blockedUsersIds = new HashSet<>();
    public void setBlockedUsersIds(Set<Long> blockedUsersIds) {
        this.blockedUsersIds = blockedUsersIds;
        notifyDataSetChanged();
    }
    public UsersListAdatper(Context context, ArrayList<UserInformationDTO> resource) {
        super(context, R.layout.user_info_card);
        this.users=resource;
    }

    private UsersListAdatper.OnActionListener listener;
    public interface OnActionListener {
        void onBlock(UserInformationDTO request, int position);
    }
    public void setOnActionListener(UsersListAdatper.OnActionListener listener) {
        this.listener = listener;
    }
    @Override
    public int getCount(){
        return users.size();
    }

    @Nullable
    @Override
    public UserInformationDTO getItem(int position){
        return users.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserInformationDTO user = users.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_info_card, parent, false);
        }
        TextView email= convertView.findViewById(R.id.email);
        TextView fullName= convertView.findViewById(R.id.first_last_name);
        Button block=convertView.findViewById(R.id.blockBtn);
        if(user!=null){
            email.setText(user.getEmail());
            fullName.setText(user.getFirstName()+" "+user.getLastName());
            if(blockedUsersIds.contains(user.getId())){
                block.setEnabled(false);
                block.setBackgroundTintList( ColorStateList.valueOf(Color.GRAY));
                block.setText("Blocked");
            }else{

                    block.setEnabled(true);
                    block.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(block.getContext(), R.color.primary_orange)));
                    block.setText("Block");
                    block.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onBlock(user, position);
                        }
                    });
            }
        }


        return  convertView;
    }
}
