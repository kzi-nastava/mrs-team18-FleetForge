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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.DriverProfileChangeRequest;
import com.ognjen.fleetforge.model.VehicleInformationChangeRequest;

import java.util.ArrayList;

public class VehicleChangesListAdapter extends ArrayAdapter<VehicleInformationChangeRequest> {
    private ArrayList<VehicleInformationChangeRequest> vehicleChanges;
    private VehicleChangesListAdapter.OnActionListener listener;
    public interface OnActionListener {
        void onAccept(VehicleInformationChangeRequest request, int position);
        void onReject(VehicleInformationChangeRequest request, int position);
    }
    public void setOnActionListener(VehicleChangesListAdapter.OnActionListener listener) {
        this.listener = listener;
    }
    public VehicleChangesListAdapter(@NonNull Context context, ArrayList<VehicleInformationChangeRequest> changes) {
        super(context, R.layout.vehicle_change_info_card);
        this.vehicleChanges=changes;
    }

    @Override
    public int getCount(){
        return vehicleChanges.size();
    }

    @Nullable
    @Override
    public VehicleInformationChangeRequest getItem(int position){
        return vehicleChanges.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        VehicleInformationChangeRequest vICR=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.vehicle_change_info_card,parent,false);
        }
        TextView showMoreInfo= convertView.findViewById(R.id.showMoreInfo);
        TextView driverName=convertView.findViewById(R.id.driverNameCard);
        TextView oldModel=convertView.findViewById(R.id.oldModel);
        TextView oldType=convertView.findViewById(R.id.oldType);
        TextView oldRegNumber=convertView.findViewById(R.id.oldRegNumber);
        TextView oldSpace=convertView.findViewById(R.id.oldSpace);
        TextView oldBabySeat=convertView.findViewById(R.id.oldBabySeat);
        TextView oldPetFriendly=convertView.findViewById(R.id.oldPetFriendly);

        TextView newModel=convertView.findViewById(R.id.newModel);
        TextView newType=convertView.findViewById(R.id.newType);
        TextView newRegNumber=convertView.findViewById(R.id.newRegNumber);
        TextView newSpace=convertView.findViewById(R.id.newSpace);
        TextView newBabySeat=convertView.findViewById(R.id.newBabySeat);
        TextView newPetFriendly=convertView.findViewById(R.id.newPetFriendly);

        LinearLayout moreInfoItems=convertView.findViewById(R.id.moreInfoItems);

        if(vICR!=null){
            driverName.setText(vICR.getFirstName()+" "+vICR.getLastName());
            oldModel.setText(vICR.getOldModel());
            oldType.setText(vICR.getOldType().toString());
            oldRegNumber.setText(vICR.getOldRegistrationNumber());
            oldSpace.setText(String.valueOf(vICR.getOldSpace()));
            oldBabySeat.setText("Baby seat: "+vICR.isOldBabySeat());
            oldPetFriendly.setText("Pet friendly: "+vICR.isOldPetFriendly());

            newModel.setText(vICR.getNewModel());
            newType.setText(vICR.getNewType().toString());
            newRegNumber.setText(vICR.getNewRegistrationNumber());
            newSpace.setText(String.valueOf(vICR.getNewSpace()));
            newBabySeat.setText("Baby seat: "+vICR.isNewBabySeat());
            newPetFriendly.setText("Pet friendly: "+vICR.isNewPetFriendly());

            if(vICR.isExpanded()){
                moreInfoItems.setVisibility(View.VISIBLE);
            } else {
                moreInfoItems.setVisibility(View.GONE);
            }

            showMoreInfo.setOnClickListener(v -> {
                vICR.setExpanded(!vICR.isExpanded());
                notifyDataSetChanged();
            });
        }

        Button change=convertView.findViewById(R.id.changeBtn);
        Button cancel=convertView.findViewById(R.id.cancelBtn);

        change.setOnClickListener(v -> {
            if(listener!=null){
                listener.onAccept(vICR,position);
            }
        });

        cancel.setOnClickListener(v -> {
            if(listener!=null){
                listener.onReject(vICR,position);
            }
        });
        return convertView;
    }
}
