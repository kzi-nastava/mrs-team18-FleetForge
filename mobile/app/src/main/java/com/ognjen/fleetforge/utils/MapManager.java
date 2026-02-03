package com.ognjen.fleetforge.utils;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapboxMap;

public class MapManager {

    private final MapboxMap mapboxMap;

    private static final double DEFAULT_LAT = 45.2515;
    private static final double DEFAULT_LNG = 19.8369;
    private static final double DEFAULT_ZOOM = 13.0;

    public MapManager(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    public void centerOnDefault() {
        CameraOptions camera = new CameraOptions.Builder()
                .center(Point.fromLngLat(DEFAULT_LNG, DEFAULT_LAT))
                .zoom(DEFAULT_ZOOM)
                .build();
        mapboxMap.setCamera(camera);
    }

}