package com.ognjen.fleetforge.dtos.photon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotonResponse {
    @SerializedName("features")
    private List<Feature> features;

    public List<Feature> getFeatures()
    {
        return features;
    }

    public static class Feature {
        @SerializedName("properties")
        private Properties properties;

        @SerializedName("geometry")
        private Geometry geometry;

        public Properties getProperties()
        {
            return properties;
        }
        public Geometry getGeometry()
        {
            return geometry;
        }
    }

    public static class Properties {
        @SerializedName("name") private String name;
        @SerializedName("street") private String street;
        @SerializedName("housenumber") private String housenumber;
        @SerializedName("city") private String city;

        public String getDisplayName() {
            StringBuilder sb = new StringBuilder();
            if (name != null) sb.append(name);
            if (street != null) sb.append(", ").append(street);
            if (housenumber != null) sb.append(" ").append(housenumber);
            if (city != null) sb.append(", ").append(city);
            return sb.toString();
        }
    }

    public static class Geometry {
        @SerializedName("coordinates")
        private List<Double> coordinates;
        public double getLat() { return coordinates.get(1); }
        public double getLon() { return coordinates.get(0); }
    }
}
