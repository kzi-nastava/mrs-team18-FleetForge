package com.ognjen.fleetforge.adapters;

import android.annotation.SuppressLint;
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

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.dtos.admin.AdminRideDetailsDto;
import com.ognjen.fleetforge.dtos.admin.AdminRideHistoryDto;
import com.ognjen.fleetforge.dtos.passenger.PassengerRideDetailsDto;
import com.ognjen.fleetforge.model.CalculatedRoute;
import com.ognjen.fleetforge.utils.MapManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class AdminHistoryAdapter extends ArrayAdapter<AdminRideHistoryDto> {
    private static final String BaseUrl = "http://" + BuildConfig.IP_ADDR + ":8080";

    private final RoutingService routingService;
    private static final String MAPBOX_API_KEY = BuildConfig.MAPBOX_API_KEY;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault());

    private Long expandedRideId = -1L;
    private AdminRideDetailsDto detailedData;
    private OnActionListener listener;

    public interface OnActionListener {
        void onDetailsClicked(AdminRideHistoryDto ride);
    }

    public AdminHistoryAdapter(Context context, ArrayList<AdminRideHistoryDto> data) {
        super(context, R.layout.admin_history_card, data);
        this.routingService = new RoutingService(MAPBOX_API_KEY);
    }

    public void setOnActionListener(OnActionListener listener) {
        this.listener = listener;
    }

    public void setExpandedRideId(Long id) {
        this.expandedRideId = id;
        notifyDataSetChanged();
    }

    public void setDetailedData(AdminRideDetailsDto details) {
        this.detailedData = details;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        AdminRideHistoryDto ride = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_history_card, parent, false);
            holder = new ViewHolder(convertView, getContext());
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

    private void bindRideInfo(ViewHolder holder, AdminRideHistoryDto ride) {
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

    }

    private void setRatingStars(TextView textView, Double rating, String label) {
        double safeRating = rating != null ? rating : 0.0;

        int fullStars = (int) Math.round(safeRating);
        int maxStars = 5;

        StringBuilder stars = new StringBuilder(label + " ");

        for (int i = 1; i <= maxStars; i++) {
            if (i <= fullStars) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }

        textView.setText(stars.toString());

        textView.setTextColor(Color.parseColor("#FFB300")); //  yellow
    }


    @SuppressLint("SetTextI18n")
    private void bindExpandedView(ViewHolder holder, AdminRideHistoryDto ride) {
        boolean isExpanded = ride.getRideId().equals(expandedRideId);
        holder.expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        holder.btnDetails.setOnClickListener(v -> {
            if (listener != null) listener.onDetailsClicked(ride);
        });

        holder.btnHide.setOnClickListener(v -> {
            expandedRideId = -1L;
            notifyDataSetChanged();
        });

        // Load detailed data and setup map
        if (isExpanded && detailedData != null &&
                detailedData.getId().equals(ride.getRideId())) {

            // Driver name
            holder.driverName.setText(
                    detailedData.getDriver().getFirstName() + " " +
                            detailedData.getDriver().getLastName());

            // Profile picture
            String imgUrl = BaseUrl + detailedData.getDriver().getProfileImage();

            Glide.with(getContext())
                    .load(imgUrl)
                    .into(holder.driverImage);

            // Driver phone
            holder.driverPhone.setText(
                    detailedData.getDriver().getPhoneNumber());

            // Distance
            holder.distance.setText(String.format(
                    Locale.getDefault(),
                    "Distance: %.1f km",
                    detailedData.getTotalDistance()));

            // Duration
            holder.duration.setText(String.format(
                    Locale.getDefault(),
                    "Duration: %d min",
                    detailedData.getEstimatedDuration()));

            // Cost
            holder.price.setText(String.format(
                    Locale.getDefault(),
                    "%.0f RSD",
                    detailedData.getTotalCost()));

            // Vehicle type
            holder.vehicleType.setText(
                    "Vehicle: " + detailedData.getVehicleType());

            // Ratings
            AdminRideDetailsDto.Ratings ratings = detailedData.getRatings();

            Double driverScore = (ratings != null) ? ratings.getDriverRating() : 0.0;
            Double vehicleScore = (ratings != null) ? ratings.getVehicleRating() : 0.0;

            setRatingStars(holder.driverRating, driverScore, "Driver:");
            setRatingStars(holder.vehicleRating, vehicleScore, "Vehicle:");

            StringBuilder passengers = new StringBuilder("Passengers: ");
            if (detailedData.getMainPassenger() != null) {
                passengers.append(detailedData.getMainPassenger().getFirstName()).append(" ").append(detailedData.getMainPassenger().getLastName());
            }
            if (detailedData.getLinkedPassengers() != null && !detailedData.getLinkedPassengers().isEmpty()) {
                for (AdminRideDetailsDto.PassengerInfo p : detailedData.getLinkedPassengers()) {
                    passengers.append(", ").append(p.getFirstName()).append(" ").append(p.getLastName());
                }
            }
            holder.tvPassengers.setText(passengers.toString());

            if ("CANCELLED".equalsIgnoreCase(detailedData.getStatus())) {
                holder.tvCancellation.setVisibility(View.VISIBLE);
                holder.tvCancellation.setText(String.format("Cancelled by %s: %s",
                        detailedData.getCancelledBy(),
                        detailedData.getCancellationReason()));
            } else {
                holder.tvCancellation.setVisibility(View.GONE);
            }

            if (detailedData.getHasInconsistencies() && detailedData.getInconsistencies() != null) {
                holder.inconsistencies.setVisibility(View.VISIBLE);
                StringBuilder incMsg = new StringBuilder("Inconsistencies Detected:\n");
                for (var report : detailedData.getInconsistencies()) {
                    incMsg.append("- ").append(report.getMessage()).append("\n");
                }
                holder.inconsistencies.setText(incMsg.toString().trim());
            } else {
                holder.inconsistencies.setVisibility(View.GONE);
            }

            // Date & time formatting
            try {
                if (detailedData.getStartTime() != null) {
                    LocalDateTime start = LocalDateTime.parse(detailedData.getStartTime());
                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("M/d/yy, h:mm a", Locale.getDefault());
                    holder.detailDateTime.setText(start.format(formatter));
                }
            } catch (Exception e) {
                holder.detailDateTime.setText("-");
            }

            if (holder.mapManager != null) {
                setupMap(holder, detailedData);
            }
        }

    }

    private void setupMap(AdminHistoryAdapter.ViewHolder holder, AdminRideDetailsDto details) {
        MapManager mm = holder.mapManager;
        mm.clearAll();

        if (details.getStartLocation() == null || details.getEndLocation() == null) {
            mm.centerOnDefault();
            return;
        }

        double startLat = details.getStartLocation().getLatitude();
        double startLon = details.getStartLocation().getLongitude();
        double endLat   = details.getEndLocation().getLatitude();
        double endLon   = details.getEndLocation().getLongitude();

        mm.addMarker(startLat, startLon, "Start: " + details.getStartAddress(), R.drawable.ic_map_point);
        mm.addMarker(endLat,   endLon,   "End: "   + details.getEndAddress(),   R.drawable.ic_map_point);

        double centerLat = (startLat + endLat) / 2;
        double centerLon = (startLon + endLon) / 2;
        holder.mapView.getController().setZoom(13.0);
        holder.mapView.getController().setCenter(
                new org.osmdroid.util.GeoPoint(centerLat, centerLon));

        List<com.ognjen.fleetforge.model.GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(new com.ognjen.fleetforge.model.GeoPoint(startLat, startLon));
        routePoints.add(new com.ognjen.fleetforge.model.GeoPoint(endLat,   endLon));

        new Thread(() -> {
            try {
                CalculatedRoute calculatedRoute = routingService.calculateRoute(routePoints);

                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    List<org.osmdroid.util.GeoPoint> osmPoints = new ArrayList<>();
                    for (com.ognjen.fleetforge.model.GeoPoint pt : calculatedRoute.getCoordinates()) {
                        osmPoints.add(new org.osmdroid.util.GeoPoint(
                                pt.getLatitude(), pt.getLongitude()));
                    }

                    int colorPrimary = getContext().getResources().getColor(R.color.colorPrimary);
                    mm.drawRoute(osmPoints, colorPrimary);
                    holder.mapView.invalidate();
                });

            } catch (Exception e) {
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    List<org.osmdroid.util.GeoPoint> fallback = new ArrayList<>();
                    fallback.add(new org.osmdroid.util.GeoPoint(startLat, startLon));
                    fallback.add(new org.osmdroid.util.GeoPoint(endLat,   endLon));
                    int colorPrimary = getContext().getResources().getColor(R.color.colorPrimary);
                    mm.drawRoute(fallback, colorPrimary);
                });
            }
        }).start();
    }

    private int getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "COMPLETED": return Color.parseColor("#4CAF50");
            case "CANCELLED": return Color.parseColor("#F44336");
            default: return Color.GRAY;
        }
    }

    private static class ViewHolder {
        final TextView startAddr, endAddr, statusTime, tvRideDate;
        final TextView driverPhone, duration, vehicleType,
                driverRating, vehicleRating, inconsistencies, detailDateTime;
        final ShapeableImageView driverImage;
        final TextView driverName, price, distance, tvPassengers, tvCancellation;
        final ImageButton heart;
        final Button btnDetails, btnHide;
        final View expandedLayout;
        final MapView mapView;
        final MapManager mapManager; // Keep reference to the manager

        ViewHolder(View view, Context context) {
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

            driverImage = view.findViewById(R.id.iv_driver_img);
            driverPhone = view.findViewById(R.id.tv_driver_phone);
            duration = view.findViewById(R.id.tv_detail_duration);
            vehicleType = view.findViewById(R.id.tv_detail_vehicle);
            driverRating = view.findViewById(R.id.tv_driver_rating);
            vehicleRating = view.findViewById(R.id.tv_vehicle_rating);
            inconsistencies = view.findViewById(R.id.tv_inconsistencies);
            detailDateTime = view.findViewById(R.id.tv_detail_datetime);
            tvPassengers = view.findViewById(R.id.tv_passengers);
            tvCancellation = view.findViewById(R.id.tv_cancellation_info);

            mapView = view.findViewById(R.id.map_view);
            // Initialize MapManager once per ViewHolder
            mapManager = new MapManager(mapView, context);
        }
    }
}