package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.dtos.admin.ActiveRideDTO;
import com.ognjen.fleetforge.dtos.admin.ActiveRideDetailsDTO;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.utils.MapManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActiveRidesAdapter extends ArrayAdapter<ActiveRideDTO> {

    private static final String BaseUrl = "http://" + BuildConfig.IP_ADDR + ":8080";
    private static final String MAPBOX_API_KEY = BuildConfig.MAPBOX_API_KEY;
    private final Context context;
    private final List<ActiveRideDTO> rides;
    private OnDetailsClickListener onDetailsClickListener;
    private Long expandedRideId = null;
    private ActiveRideDetailsDTO detailedData = null;
    private final RoutingService routingService;

    public interface OnDetailsClickListener {
        void onDetailsClick(ActiveRideDTO ride);
    }

    public ActiveRidesAdapter(@NonNull Context context, @NonNull List<ActiveRideDTO> rides) {
        super(context, 0, rides);
        this.context = context;
        this.rides = rides;
        this.routingService = new RoutingService(MAPBOX_API_KEY);
    }

    public void setOnDetailsClickListener(OnDetailsClickListener listener) {
        this.onDetailsClickListener = listener;
    }

    public void setExpandedRideId(Long rideId) {
        this.expandedRideId = rideId;
        notifyDataSetChanged();
    }

    public void setDetailedData(ActiveRideDetailsDTO details) {
        this.detailedData = details;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_active_ride, parent, false);
            holder = new ViewHolder(convertView, context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ActiveRideDTO ride = rides.get(position);
        boolean isExpanded = expandedRideId != null && expandedRideId.equals(ride.getRideId());

        holder.driverName.setText(ride.getDriverFirstName() + " " + ride.getDriverLastName());
        holder.startAddress.setText(ride.getStartAddress());
        holder.endAddress.setText(ride.getEndAddress());
        holder.startTime.setText("Started: " + formatTime(ride.getStartTime()));
        holder.passengerCount.setText("👥 " + ride.getPassengerCount());

        if (ride.getPanicActivated() != null && ride.getPanicActivated()) {
            holder.panicIndicator.setVisibility(View.VISIBLE);
            holder.panicIndicator.setText("⚠️ PANIC ACTIVATED");
        } else {
            holder.panicIndicator.setVisibility(View.GONE);
        }

        if (isExpanded) {
            holder.expandedLayout.setVisibility(View.VISIBLE);
            setupExpandedView(holder, ride);
        } else {
            holder.expandedLayout.setVisibility(View.GONE);
        }

        holder.btnDetails.setOnClickListener(v -> {
            if (onDetailsClickListener != null) {
                onDetailsClickListener.onDetailsClick(ride);
            }
        });

        holder.btnHide.setOnClickListener(v -> {
            expandedRideId = null;
            notifyDataSetChanged();
        });

        return convertView;
    }

    private void setupExpandedView(ViewHolder holder, ActiveRideDTO ride) {
        if (detailedData != null && detailedData.getRideId().equals(ride.getRideId())) {
            holder.tvDriverName.setText(detailedData.getDriverFirstName() + " " + detailedData.getDriverLastName());
            holder.tvDriverPhone.setText(detailedData.getDriverPhoneNumber());

            if (detailedData.getDriverProfileImage() != null && !detailedData.getDriverProfileImage().isEmpty()) {
                String imgUrl = BaseUrl + detailedData.getDriverProfileImage();
                Glide.with(context)
                        .load(imgUrl)
                        .into(holder.ivDriverImg);
            }

            if (detailedData.getVehicleType() != null) {
                holder.tvVehicleType.setText("Vehicle: " + detailedData.getVehicleType().toString());
            }

            if (detailedData.getPanicActivated() != null && detailedData.getPanicActivated()) {
                holder.tvPanicStatus.setVisibility(View.VISIBLE);
                String panicText = "⚠️ PANIC ACTIVATED";
                if (detailedData.getPanicActivatedAt() != null) {
                    panicText += " at " + formatTime(detailedData.getPanicActivatedAt());
                }
                holder.tvPanicStatus.setText(panicText);
            } else {
                holder.tvPanicStatus.setVisibility(View.GONE);
            }

            holder.passengersContainer.removeAllViews();
            if (detailedData.getPassengers() != null) {
                for (ActiveRideDetailsDTO.PassengerDTO passenger : detailedData.getPassengers()) {
                    TextView passengerView = new TextView(context);
                    passengerView.setText("👤 " + passenger.getFirstName() + " " + passenger.getLastName());
                    passengerView.setTextSize(14);
                    passengerView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                    passengerView.setPadding(0, 4, 0, 4);
                    holder.passengersContainer.addView(passengerView);
                }
            }

            setupMap(holder, detailedData);
        }
    }

    private void setupMap(ViewHolder holder, ActiveRideDetailsDTO details) {
        holder.mapManager.clearAll();

        if (details.getCurrentLocation() != null && details.getEndLocation() != null) {
            GeoPoint driverPoint = new GeoPoint(
                    details.getCurrentLocation().getLatitude(),
                    details.getCurrentLocation().getLongitude()
            );
            holder.mapManager.addMarker(
                    driverPoint.getLatitude(),
                    driverPoint.getLongitude(),
                    "Driver",
                    R.drawable.ic_current_taxi
            );

            GeoPoint endPoint = new GeoPoint(
                    details.getEndLocation().getLatitude(),
                    details.getEndLocation().getLongitude()
            );
            holder.mapManager.addMarker(
                    endPoint.getLatitude(),
                    endPoint.getLongitude(),
                    "Destination",
                    R.drawable.ic_map_point
            );

            holder.mapView.getController().setCenter(driverPoint);
            holder.mapView.getController().setZoom(14.0);

            MapManager finalMapManager = holder.mapManager;
            new Thread(() -> {
                try {
                    com.ognjen.fleetforge.model.GeoPoint start = new com.ognjen.fleetforge.model.GeoPoint(
                            details.getCurrentLocation().getLatitude(),
                            details.getCurrentLocation().getLongitude()
                    );
                    com.ognjen.fleetforge.model.GeoPoint end = new com.ognjen.fleetforge.model.GeoPoint(
                            details.getEndLocation().getLatitude(),
                            details.getEndLocation().getLongitude()
                    );

                    CalculatedRoute route = routingService.calculateRoute(start, end);

                    if (route != null && route.getCoordinates() != null) {
                        List<GeoPoint> osmPoints = new ArrayList<>();
                        for (com.ognjen.fleetforge.model.GeoPoint point : route.getCoordinates()) {
                            osmPoints.add(new GeoPoint(point.getLatitude(), point.getLongitude()));
                        }

                        ((android.app.Activity) context).runOnUiThread(() -> {
                            if (finalMapManager != null) {
                                finalMapManager.drawRoute(osmPoints, 0xFFFF9900);
                            }
                        });
                    }
                } catch (IOException e) {
                    android.util.Log.e("ActiveRidesAdapter", "Failed to calculate route", e);
                }
            }).start();
        }
    }

    private String formatTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "";
        }

        try {
            String cleanedDateTime = dateTimeString;
            if (dateTimeString.contains(".")) {
                cleanedDateTime = dateTimeString.substring(0, dateTimeString.indexOf("."));
            }

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            try {
                Date date = isoFormat.parse(cleanedDateTime);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sqlFormat.parse(cleanedDateTime);
                if (date != null) {
                    return outputFormat.format(date);
                }
            }
        } catch (ParseException e) {
            android.util.Log.e("ActiveRidesAdapter", "Error parsing time: " + dateTimeString, e);
        }

        return dateTimeString;
    }

    private static class ViewHolder {
        TextView driverName;
        TextView startAddress;
        TextView endAddress;
        TextView startTime;
        TextView passengerCount;
        TextView panicIndicator;
        Button btnDetails;

        LinearLayout expandedLayout;
        MapView mapView;
        MapManager mapManager;
        Button btnHide;

        ImageView ivDriverImg;
        TextView tvDriverName;
        TextView tvDriverPhone;
        TextView tvVehicleType;
        TextView tvPanicStatus;
        LinearLayout passengersContainer;

        ViewHolder(View view, Context context) {
            driverName = view.findViewById(R.id.driver_name);
            startAddress = view.findViewById(R.id.start_address);
            endAddress = view.findViewById(R.id.end_address);
            startTime = view.findViewById(R.id.start_time);
            passengerCount = view.findViewById(R.id.passenger_count);
            panicIndicator = view.findViewById(R.id.panic_indicator);
            btnDetails = view.findViewById(R.id.btn_details);

            expandedLayout = view.findViewById(R.id.ll_expanded_view);
            mapView = view.findViewById(R.id.map_view);
            btnHide = view.findViewById(R.id.btn_hide_details);

            ivDriverImg = view.findViewById(R.id.iv_driver_img);
            tvDriverName = view.findViewById(R.id.tv_driver_name);
            tvDriverPhone = view.findViewById(R.id.tv_driver_phone);
            tvVehicleType = view.findViewById(R.id.tv_vehicle_type);
            tvPanicStatus = view.findViewById(R.id.tv_panic_status);
            passengersContainer = view.findViewById(R.id.passengers_container);

            mapManager = new MapManager(mapView, context);
        }
    }
}

