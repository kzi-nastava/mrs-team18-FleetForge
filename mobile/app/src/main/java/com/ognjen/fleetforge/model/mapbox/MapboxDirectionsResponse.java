package com.ognjen.fleetforge.model.mapbox;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response model for Mapbox Directions API
 * https://docs.mapbox.com/api/navigation/directions/
 */
public class MapboxDirectionsResponse {
    @SerializedName("routes")
    private List<Route> routes;

    @SerializedName("code")
    private String code;

    public List<Route> getRoutes() {
        return routes;
    }

    public String getCode() {
        return code;
    }

    public static class Route {
        @SerializedName("geometry")
        private Geometry geometry;

        @SerializedName("distance")
        private double distance;

        @SerializedName("duration")
        private double duration;

        public Geometry getGeometry() {
            return geometry;
        }

        public double getDistance() {
            return distance;
        }

        public double getDuration() {
            return duration;
        }

        public double getDistanceKm() {
            return distance / 1000.0;
        }

        public int getDurationMinutes() {
            return (int) Math.round(duration / 60.0);
        }
    }

    public static class Geometry {
        @SerializedName("coordinates")
        private List<List<Double>> coordinates;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }
    }
}