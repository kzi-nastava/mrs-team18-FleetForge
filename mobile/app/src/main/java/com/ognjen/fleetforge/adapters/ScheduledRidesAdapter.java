package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.ride.ScheduledRideDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ScheduledRidesAdapter extends ArrayAdapter<ScheduledRideDto> {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a", Locale.getDefault());

    public interface OnCancelListener {
        void onCancel(ScheduledRideDto ride);
    }

    private OnCancelListener cancelListener;

    public ScheduledRidesAdapter(Context context, ArrayList<ScheduledRideDto> data) {
        super(context, R.layout.scheduled_ride_card, data);
    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.cancelListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScheduledRideDto ride = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.scheduled_ride_card, parent, false);
        }

        TextView tvPickup = convertView.findViewById(R.id.tv_pickup_address);
        TextView tvDropoff = convertView.findViewById(R.id.tv_dropoff_address);
        TextView tvTime = convertView.findViewById(R.id.tv_scheduled_time);
        TextView tvCost = convertView.findViewById(R.id.tv_estimated_cost);
        Button btnCancel = convertView.findViewById(R.id.btn_cancel_scheduled);

        tvPickup.setText(ride.getPickup());
        tvDropoff.setText(ride.getDropoff());

        try {
            if (ride.getScheduledTime() != null) {
                LocalDateTime dateTime = LocalDateTime.parse(ride.getScheduledTime(), ISO_FORMATTER);
                tvTime.setText(dateTime.format(DISPLAY_FORMATTER));
            } else {
                tvTime.setText("Time not set");
            }
        } catch (Exception e) {
            tvTime.setText(ride.getScheduledTime()); // Fallback to raw string if parsing fails
        }
        tvCost.setText(String.format(Locale.getDefault(), "%.0f RSD", ride.getEstimatedCost()));

        btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) cancelListener.onCancel(ride);
        });

        return convertView;
    }
}