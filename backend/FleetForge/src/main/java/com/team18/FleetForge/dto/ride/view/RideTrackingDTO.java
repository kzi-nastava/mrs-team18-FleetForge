package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideTrackingDTO {

    private Long rideId;
    private String status;
    private GeoPoint currentLocation;
    private Integer estimatedArrivalMinutes;
    private RouteInfoDTO route;
    private DriverInfoDTO driver;
    private Boolean panicActivated;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RouteInfoDTO {
        private GeoPoint startLocation;
        private String startAddress;
        private GeoPoint endLocation;
        private String endAddress;
        private List<WaypointDTO> waypoints;
        private Double totalDistanceKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WaypointDTO {
        private GeoPoint location;
        private String address;
        private Integer order;
    }

}