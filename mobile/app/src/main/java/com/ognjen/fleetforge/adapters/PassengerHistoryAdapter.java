package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideDetailsDto;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideHistoryDto;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PassengerHistoryAdapter extends ArrayAdapter<PassengerRideHistoryDto> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());

    private Long expandedRideId = -1L;
    private PassengerRideDetailsDto detailedData;
    private Set<Long> favoriteRideIds = new HashSet<>();
    private OnActionListener listener;

    public interface OnActionListener {
        void onHeart(PassengerRideHistoryDto ride, ImageButton heartBtn);
        void onDetailsClicked(PassengerRideHistoryDto ride);
    }

    public PassengerHistoryAdapter(Context context, ArrayList<PassengerRideHistoryDto> data) {
        super(context, R.layout.passenger_history_card, data);
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void setExpandedRideId(Long id) {
        this.expandedRideId = id;
        notifyDataSetChanged();
    }

    public void setDetailedData(PassengerRideDetailsDto details) {
        this.detailedData = details;
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<Long> favoriteRideIds) {
        this.favoriteRideIds = favoriteRideIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        PassengerRideHistoryDto ride = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passenger_history_card, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (ride != null) {
            bindRideInfo(holder, ride);
            bindExpandedView(holder, ride);
        }

        return convertView;
    }

    private void bindRideInfo(ViewHolder holder, PassengerRideHistoryDto ride) {
        holder.startAddr.setText(ride.getStartAddress());
        holder.endAddr.setText(ride.getEndAddress());

        // Status and Colors
        String status = ride.getStatus() != null ? ride.getStatus() : "UNKNOWN";
        holder.statusTime.setTextColor(getStatusColor(status));

        // Date & Time Formatting
        try {
            if (ride.getStartTime() != null) {
                LocalDateTime start = LocalDateTime.parse(ride.getStartTime());
                holder.tvRideDate.setText(start.format(DATE_FORMATTER));

                String timeDisplay = start.format(TIME_FORMATTER);
                if (ride.getEndTime() != null) {
                    LocalDateTime end = LocalDateTime.parse(ride.getEndTime());
                    timeDisplay += " - " + end.format(TIME_FORMATTER);
                }
                holder.statusTime.setText(String.format("%s • %s", status, timeDisplay));
            }
        } catch (Exception e) {
            holder.tvRideDate.setText("---");
            holder.statusTime.setText(status);
        }

        // Favorites
        holder.heart.setSelected(favoriteRideIds.contains(ride.getRideId()));
        holder.heart.setOnClickListener(v -> {
            if (listener != null) listener.onHeart(ride, holder.heart);
        });
    }

    private void bindExpandedView(ViewHolder holder, PassengerRideHistoryDto ride) {
        boolean isExpanded = ride.getRideId().equals(expandedRideId);
        holder.expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.btnDetails.setOnClickListener(v -> {
            if (listener != null) listener.onDetailsClicked(ride);
        });

        holder.btnHide.setOnClickListener(v -> {
            expandedRideId = -1L;
            notifyDataSetChanged();
        });

        // Load detailed data into the expanded view if available
        if (isExpanded && detailedData != null && detailedData.getId().equals(ride.getRideId())) {
            holder.driverName.setText(String.format("%s %s", detailedData.getDriver().firstName, detailedData.getDriver().lastName));
            holder.price.setText(String.format(Locale.getDefault(), "Cost: %.2f RSD", detailedData.getTotalCost()));
            holder.distance.setText(String.format(Locale.getDefault(), "Distance: %.2f km", detailedData.getTotalDistance()));

            if (holder.mapView != null) {
                setupMiniMap(holder.mapView, detailedData);
            }
        }
    }

    private int getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "COMPLETED": return Color.parseColor("#4CAF50");
            case "CANCELLED": return Color.parseColor("#F44336");
            default: return Color.GRAY;
        }
    }

    private void setupMiniMap(MapView map, PassengerRideDetailsDto details) {
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(details.getStartLocation().latitude, details.getStartLocation().longitude);
        map.getController().setCenter(startPoint);

        map.getOverlays().clear(); // Clear old markers
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setTitle("Start");
        map.getOverlays().add(startMarker);
        map.invalidate(); // Refresh map
    }

    private static class ViewHolder {
        final TextView startAddr, endAddr, statusTime, tvRideDate;
        final TextView driverName, price, distance;
        final ImageButton heart;
        final Button btnDetails, btnHide;
        final View expandedLayout;
        final MapView mapView;

        ViewHolder(View view) {
            startAddr = view.findViewById(R.id.tv_start_address);
            endAddr = view.findViewById(R.id.tv_end_address);
            statusTime = view.findViewById(R.id.tv_status_time);
            tvRideDate = view.findViewById(R.id.tv_ride_date);
            heart = view.findViewById(R.id.heart_button);

            expandedLayout = view.findViewById(R.id.ll_expanded_view);
            btnDetails = view.findViewById(R.id.btn_details);
            btnHide = view.findViewById(R.id.btn_hide_details);

            driverName = view.findViewById(R.id.tv_driver_name);
            price = view.findViewById(R.id.tv_detail_price);
            distance = view.findViewById(R.id.tv_detail_distance);
            mapView = view.findViewById(R.id.map_view);
        }
    }
}