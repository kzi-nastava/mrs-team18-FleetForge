package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.DriverProfileChangeRequest;

import java.util.ArrayList;

public class DriverChangesListAdapter extends ArrayAdapter<DriverProfileChangeRequest> {
    private ArrayList<DriverProfileChangeRequest> profileChanges;
    private OnActionListener listener;
    public interface OnActionListener {
        void onAccept(DriverProfileChangeRequest request, int position);
        void onReject(DriverProfileChangeRequest request, int position);
    }
    public DriverChangesListAdapter(Context context, ArrayList<DriverProfileChangeRequest> profileChanges){
        super(context, R.layout.driver_change_info_card);
        this.profileChanges=profileChanges;
    }
    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount(){
        return profileChanges.size();
    }

    @Nullable
    @Override
    public DriverProfileChangeRequest getItem(int position){
        return profileChanges.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        DriverProfileChangeRequest dPC=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.driver_change_info_card,parent,false);
        }
        TextView showMoreInfo= convertView.findViewById(R.id.showMoreInfo);
        TextView driverName=convertView.findViewById(R.id.driverNameCard);
        TextView oldFirstName=convertView.findViewById(R.id.oldFirstName);
        TextView newFirstName=convertView.findViewById(R.id.newFirstName);
        TextView oldLastName=convertView.findViewById(R.id.oldLastName);
        TextView newLastName=convertView.findViewById(R.id.newLastName);
        TextView oldEmail=convertView.findViewById(R.id.oldEmail);
        TextView newEmail=convertView.findViewById(R.id.newEmail);
        TextView oldAddress=convertView.findViewById(R.id.oldAddress);
        TextView newAddress=convertView.findViewById(R.id.newAddress);
        TextView oldNumber=convertView.findViewById(R.id.oldNumber);
        TextView newNumber=convertView.findViewById(R.id.newNumber);
        Button acceptBtn=convertView.findViewById(R.id.changeBtn);
        Button rejectBtn= convertView.findViewById(R.id.cancelBtn);

        acceptBtn.setOnClickListener(v -> {
            if(listener!=null){
                listener.onAccept(dPC,position);
            }
        });

        rejectBtn.setOnClickListener(v -> {
            if(listener!=null){
                listener.onReject(dPC,position);
            }
        });

        LinearLayout moreInfoItems=convertView.findViewById(R.id.moreInfoItems);

        if(dPC!=null){
            driverName.setText(dPC.getOldFirstName()+" "+dPC.getOldLastName());
            oldFirstName.setText(dPC.getOldFirstName());
            newFirstName.setText(dPC.getNewFirstName());
            oldLastName.setText(dPC.getOldLastName());
            newLastName.setText(dPC.getNewLastName());
            oldEmail.setText(dPC.getOldEmail());
            newEmail.setText(dPC.getNewEmail());
            oldAddress.setText(dPC.getOldAddress());
            newAddress.setText(dPC.getNewAddress());
            oldNumber.setText(dPC.getOldPhoneNumber());
            newNumber.setText(dPC.getNewPhoneNumber());

            if(dPC.isExpanded()){
                moreInfoItems.setVisibility(View.VISIBLE);
            } else {
                moreInfoItems.setVisibility(View.GONE);
            }

            showMoreInfo.setOnClickListener(v -> {
                dPC.setExpanded(!dPC.isExpanded());
                notifyDataSetChanged();
            });
        }
        return convertView;
    }
}
