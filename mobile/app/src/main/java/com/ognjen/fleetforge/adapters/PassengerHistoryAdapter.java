package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PassengerHistoryAdapter extends ArrayAdapter<PassengerRideHistoryDto> {
    private ArrayList<PassengerRideHistoryDto> data;
    private Set<Long> favoriteRideIds = new HashSet<>();

    public PassengerHistoryAdapter(Context context, ArrayList<PassengerRideHistoryDto> data){
        super(context, R.layout.passenger_history_card, data);
        this.data = data;
    }

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
        PassengerRideHistoryDto ride = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passenger_history_card, parent, false);
        }

        ImageButton heart = convertView.findViewById(R.id.heart_button);
        TextView startAddr = convertView.findViewById(R.id.tv_start_address);
        TextView endAddr = convertView.findViewById(R.id.tv_end_address);
        TextView statusTime = convertView.findViewById(R.id.tv_status_time);
        TextView tvRideDate = convertView.findViewById(R.id.tv_ride_date);

        if (ride != null) {
            startAddr.setText(ride.getStartAddress());
            endAddr.setText(ride.getEndAddress());

            String status = ride.getStatus() != null ? ride.getStatus() : "UNKNOWN";
            statusTime.setText(status);

            if (status.equalsIgnoreCase("COMPLETED")) {
                statusTime.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else if (status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("REJECTED")) {
                statusTime.setTextColor(Color.parseColor("#F44336")); // Red
            } else {
                statusTime.setTextColor(Color.GRAY);
            }

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());

                if (ride.getStartTime() != null) {
                    LocalDateTime start = LocalDateTime.parse(ride.getStartTime());
                    tvRideDate.setText(start.format(dateFormatter));

                    String timeDisplay = start.format(timeFormatter);

                    if (ride.getEndTime() != null) {
                        LocalDateTime end = LocalDateTime.parse(ride.getEndTime());
                        timeDisplay += " - " + end.format(timeFormatter);
                    }

                    statusTime.setText(status + " • " + timeDisplay);
                }
            } catch (Exception e) {
                tvRideDate.setText("---");
            }

            boolean isFavorite = favoriteRideIds.contains(ride.getRideId());
            heart.setSelected(isFavorite);
            heart.setOnClickListener(v -> {
                if (listener != null) listener.onHeart(ride, heart);
            });
        }
        return convertView;
    }
}
