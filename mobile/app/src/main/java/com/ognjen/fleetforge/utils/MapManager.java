package com.ognjen.fleetforge.utils;

import android.content.Context;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.ognjen.fleetforge.R;
import com.ognjen.fleetforge.model.VehicleLocation;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private static final String TAG = "MapManager";
    private final MapView mapView;
    private final Context context;

    private static final double DEFAULT_LAT = 45.2515;
    private static final double DEFAULT_LNG = 19.8369;
    private static final double DEFAULT_ZOOM = 13.0;

    // Store markers so we can clear them later
    private List<Marker> vehicleMarkers = new ArrayList<>();

    public MapManager(MapView mapView, Context context) {
        this.mapView = mapView;
        this.context = context;

        mapView.setMultiTouchControls(true);
    }
    public void centerOnDefault() {
        mapView.getController().setZoom(DEFAULT_ZOOM);
        mapView.getController().setCenter(new GeoPoint(DEFAULT_LAT, DEFAULT_LNG));
    }

    public void displayVehicles(List<VehicleLocation> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            Log.d(TAG, "No vehicles to display");
            return;
        }

        clearVehicleMarkers();

        for (VehicleLocation vehicle : vehicles) {
            if (vehicle.getCurrentLocation() != null) {
                GeoPoint point = new GeoPoint(
                        vehicle.getCurrentLocation().getLatitude(),
                        vehicle.getCurrentLocation().getLongitude()
                );

                Marker marker = new Marker(mapView);
                marker.setPosition(point);

                marker.setTitle(vehicle.getModel());
                marker.setSnippet("Type: " + vehicle.getVehicleType());

                if (vehicle.getIsAvailable()) {
                    marker.setIcon(context.getResources().getDrawable(R.drawable.ic_taxi_available));
                } else {
                    marker.setIcon(context.getResources().getDrawable(R.drawable.ic_taxi_occupied));
                }

                mapView.getOverlays().add(marker);
                vehicleMarkers.add(marker);
            }
        }

        mapView.invalidate();
    }

    public void clearVehicleMarkers() {
        for (Marker marker : vehicleMarkers) {
            mapView.getOverlays().remove(marker);
        }
        vehicleMarkers.clear();
        mapView.invalidate();
    }

    public void drawRoute(List<GeoPoint> routePoints, int color) {
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }

        Polyline routeLine = new Polyline();
        routeLine.setPoints(routePoints);
        routeLine.setColor(color);
        routeLine.setWidth(10f);

        mapView.getOverlays().add(routeLine);
        mapView.invalidate();

        Log.d(TAG, "Drew route with " + routePoints.size() + " points");
    }

    public void clearAll() {
        mapView.getOverlays().clear();
        vehicleMarkers.clear();
        mapView.invalidate();
        Log.d(TAG, "Cleared all map overlays");
    }

    public Marker addMarker(double latitude, double longitude, String title, int iconResId) {
        GeoPoint point = new GeoPoint(latitude, longitude);

        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);

        if (iconResId != 0) {
            marker.setIcon(context.getResources().getDrawable(iconResId));
        }

        mapView.getOverlays().add(marker);
        mapView.invalidate();

        return marker;
    }

    public void removeMarker(Marker marker) {
        mapView.getOverlays().remove(marker);
        mapView.invalidate();
    }

}