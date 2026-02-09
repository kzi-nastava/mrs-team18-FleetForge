package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.PassengerHistory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PassengerHistoryAdapter extends ArrayAdapter<PassengerHistory> {
    private ArrayList<PassengerHistory> data;
    private Set<Long> favoriteRideIds = new HashSet<>();
    public PassengerHistoryAdapter(Context context,ArrayList<PassengerHistory> data){
        super(context, R.layout.passenger_history_card);
        this.data=data;
    }

    private PassengerHistoryAdapter.OnActionListener listener;

    public interface OnActionListener{
        void onHeart(PassengerHistory ride, ImageButton heartBtn);
    }
    public void setOnActionListener(PassengerHistoryAdapter.OnActionListener listener) {
        this.listener = listener;
    }
    public void setFavoriteIds(Set<Long> favoriteRideIds) {
        this.favoriteRideIds = favoriteRideIds;
        notifyDataSetChanged();
    }
    @Override
    public int getCount(){
        return data.size();
    }

    @Nullable
    @Override
    public PassengerHistory getItem(int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PassengerHistory ride = data.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passenger_history_card, parent, false);
        }
        ImageButton heart= convertView.findViewById(R.id.heart_button);
        TextView title= convertView.findViewById(R.id.title);
        if(ride!=null) {
            title.setText("TITLE");
            boolean isFavorite = favoriteRideIds.contains(ride.getRideId());
            heart.setSelected(isFavorite);
            heart.setOnClickListener(v -> {
                if(listener!=null){
                    listener.onHeart(ride,heart);
                }
            });
        }


        return  convertView;
    }
}
