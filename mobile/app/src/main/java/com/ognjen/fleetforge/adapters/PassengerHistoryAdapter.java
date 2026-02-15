package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;
import com.ognjen.fleetforge.model.PassengerHistory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Change the class signature
public class PassengerHistoryAdapter extends ArrayAdapter<PassengerRideHistoryDto> {
    private ArrayList<PassengerRideHistoryDto> data;
    private Set<Long> favoriteRideIds = new HashSet<>();

    public PassengerHistoryAdapter(Context context, ArrayList<PassengerRideHistoryDto> data){
        super(context, R.layout.passenger_history_card, data);
        this.data = data;
    }

    // Update the interface
    public interface OnActionListener{
        void onHeart(PassengerRideHistoryDto ride, ImageButton heartBtn);
    }

    private OnActionListener listener;
    public void setOnActionListener(OnActionListener listener) { this.listener = listener; }

    public void setFavoriteIds(Set<Long> favoriteRideIds) {
        this.favoriteRideIds = favoriteRideIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PassengerRideHistoryDto ride = getItem(position); // Use built-in getItem
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passenger_history_card, parent, false);
        }

        ImageButton heart = convertView.findViewById(R.id.heart_button);
        TextView startAddr = convertView.findViewById(R.id.tv_start_address);
        TextView endAddr = convertView.findViewById(R.id.tv_end_address);
        TextView statusTime = convertView.findViewById(R.id.tv_status_time);

        if (ride != null) {
            // Using the DTO fields directly
            startAddr.setText(ride.getStartAddress());
            endAddr.setText(ride.getEndAddress());

            // Format status and time
            String status = ride.getStatus() != null ? ride.getStatus() : "Unknown";
            String time = ride.getStartTime() != null ? ride.getStartTime() : "";
            statusTime.setText(status + " • " + time);

            boolean isFavorite = favoriteRideIds.contains(ride.getRideId());
            heart.setSelected(isFavorite);

            heart.setOnClickListener(v -> {
                if (listener != null) listener.onHeart(ride, heart);
            });
        }
        return convertView;
    }
}
