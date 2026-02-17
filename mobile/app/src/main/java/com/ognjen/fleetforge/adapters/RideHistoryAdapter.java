package com.ognjen.fleetforge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ognjen.fleetforge.BuildConfig;
import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.api.RoutingService;
import com.ognjen.fleetforge.dtos.driver.CompletedRideDTO;
import com.ognjen.fleetforge.utils.MapManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideViewHolder> {

    private List<CompletedRideDTO> rideList;
    private Context context;
    private int expandedPosition = -1;
    private final RoutingService routingService;
    private static final String MAPBOX_API_KEY = BuildConfig.MAPBOX_API_KEY;


    public RideHistoryAdapter(List<CompletedRideDTO> rideList) {
        this.rideList = rideList != null ? rideList : new ArrayList<>();
        this.routingService = new RoutingService(MAPBOX_API_KEY);
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ride_history, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        CompletedRideDTO ride = rideList.get(position);
        holder.bind(ride, position);
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public void updateData(List<CompletedRideDTO> newRideList) {
        this.rideList = newRideList != null ? newRideList : new ArrayList<>();
        expandedPosition = -1;
        notifyDataSetChanged();
    }

    class RideViewHolder extends RecyclerView.ViewHolder {

        // Collapsed view
        private LinearLayout llCollapsedView;
        private TextView tvPassengerName;
        private TextView tvStartAddress;
        private TextView tvEndAddress;
        private Button btnDetails;

        // Expanded view
        private LinearLayout llExpandedView;
        private MapView mapView;
        private TextView tvRideId;
        private TextView tvStartTime;
        private TextView tvTotalPrice;
        private TextView tvStatus;
        private LinearLayout llPanicStatus;
        private LinearLayout llPassengersContainer;
        private Button btnCollapse;

        private MapManager mapManager;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);

            // Collapsed view
            llCollapsedView = itemView.findViewById(R.id.ll_collapsed_view);
            tvPassengerName = itemView.findViewById(R.id.tv_passenger_name);
            tvStartAddress = itemView.findViewById(R.id.tv_start_address);
            tvEndAddress = itemView.findViewById(R.id.tv_end_address);
            btnDetails = itemView.findViewById(R.id.btn_details);

            // Expanded view
            llExpandedView = itemView.findViewById(R.id.ll_expanded_view);
            mapView = itemView.findViewById(R.id.map_view);
            tvRideId = itemView.findViewById(R.id.tv_ride_id);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            llPanicStatus = itemView.findViewById(R.id.ll_panic_status);
            llPassengersContainer = itemView.findViewById(R.id.ll_passengers_container);
            btnCollapse = itemView.findViewById(R.id.btn_collapse);

            // Initialize MapManager
            mapManager = new MapManager(mapView, context);
        }

        public void bind(CompletedRideDTO ride, int position) {
            // Populate collapsed view
            tvPassengerName.setText(ride.getPassengerName() != null ? ride.getPassengerName() : "Unknown Passenger");
            tvStartAddress.setText(ride.getPickupAddress() != null ? ride.getPickupAddress() : "N/A");
            tvEndAddress.setText(ride.getDropoffAddress() != null ? ride.getDropoffAddress() : "N/A");

            // Handle expand/collapse
            boolean isExpanded = position == expandedPosition;
            llExpandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            btnDetails.setOnClickListener(v -> {
                expandedPosition = position;
                notifyItemChanged(position);

                // Setup expanded view
                setupExpandedView(ride);
            });

            btnCollapse.setOnClickListener(v -> {
                expandedPosition = -1;
                notifyItemChanged(position);
            });

            // If expanded, setup the expanded view
            if (isExpanded) {
                setupExpandedView(ride);
            }
        }

        private void setupExpandedView(CompletedRideDTO ride) {
            tvRideId.setText(String.valueOf(ride.getId()));

            tvStartTime.setText(ride.getRideDate() != null ? ride.getRideDate() : "N/A");

            tvTotalPrice.setText(String.format(Locale.US, "%.2f", ride.getTotalCost()));

            String status;
            if (ride.isCancelled()) {
                status = "Cancelled by " + ride.getCancelledBy();
                tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            } else {
                status = "Completed";
                tvStatus.setTextColor(context.getResources().getColor(R.color.text_secondary));
            }
            tvStatus.setText(status);

            if (ride.getPanicActivation() != null && ride.getPanicActivation()) {
                llPanicStatus.setVisibility(View.VISIBLE);
            } else {
                llPanicStatus.setVisibility(View.GONE);
            }

            setupMap(ride);

            setupPassengers(ride);
        }

        private void setupMap(CompletedRideDTO ride) {
            MapManager mm = this.mapManager;
            mm.clearAll();

            if (ride.getPickupLatitude() == 0 || ride.getPickupLongitude() == 0) {
                mm.centerOnDefault();
                return;
            }

            double startLat = ride.getPickupLatitude();
            double startLon = ride.getPickupLongitude();
            double endLat   = ride.getDropoffLatitude();
            double endLon   = ride.getDropoffLongitude();

            mm.addMarker(startLat, startLon, "Start: " + ride.getPickupAddress(), R.drawable.ic_map_point);
            mm.addMarker(endLat, endLon, "End: " + ride.getDropoffAddress(), R.drawable.ic_map_point);

            double centerLat = (startLat + endLat) / 2;
            double centerLon = (startLon + endLon) / 2;
            mapView.getController().setZoom(13.0);
            mapView.getController().setCenter(new org.osmdroid.util.GeoPoint(centerLat, centerLon));

            List<com.ognjen.fleetforge.model.GeoPoint> routePoints = new ArrayList<>();
            routePoints.add(new com.ognjen.fleetforge.model.GeoPoint(startLat, startLon));

            if (ride.getWaypoints() != null) {
                for (List<Double> wp : ride.getWaypoints()) {
                    if (wp.size() >= 2) {
                        routePoints.add(new com.ognjen.fleetforge.model.GeoPoint(wp.get(0), wp.get(1)));
                    }
                }
            }

            routePoints.add(new com.ognjen.fleetforge.model.GeoPoint(endLat, endLon));

            new Thread(() -> {
                try {
                    var calculatedRoute = routingService.calculateRoute(routePoints);

                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        List<org.osmdroid.util.GeoPoint> osmPoints = new ArrayList<>();
                        for (com.ognjen.fleetforge.model.GeoPoint pt : calculatedRoute.getCoordinates()) {
                            osmPoints.add(new org.osmdroid.util.GeoPoint(pt.getLatitude(), pt.getLongitude()));
                        }

                        int colorPrimary = context.getResources().getColor(R.color.colorPrimary);
                        mm.drawRoute(osmPoints, colorPrimary);
                        mapView.invalidate();
                    });

                } catch (Exception e) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        List<org.osmdroid.util.GeoPoint> fallback = new ArrayList<>();
                        fallback.add(new org.osmdroid.util.GeoPoint(startLat, startLon));
                        fallback.add(new org.osmdroid.util.GeoPoint(endLat, endLon));
                        int colorPrimary = context.getResources().getColor(R.color.colorPrimary);
                        mm.drawRoute(fallback, colorPrimary);
                        mapView.invalidate();
                    });
                }
            }).start();
        }

        private void setupPassengers(CompletedRideDTO ride) {
            llPassengersContainer.removeAllViews();

            addPassengerView(ride.getPassengerName());

            if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
                for (String passengerName : ride.getLinkedPassengers()) {
                    addPassengerView(passengerName);
                }
            }
        }

        private void addPassengerView(String passengerName) {
            View passengerView = LayoutInflater.from(context)
                    .inflate(R.layout.item_passenger_info, llPassengersContainer, false);

            TextView tvName = passengerView.findViewById(R.id.tv_passenger_name);
            tvName.setText(passengerName != null ? passengerName : "Unknown");

            llPassengersContainer.addView(passengerView);
        }
    }
}