package com.team18.FleetForge.dto.ride.view;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveRideDetailsDTO {

    private Long rideId;

    private String driverFirstName;
    private String driverLastName;
    private String driverPhoneNumber;
    private String driverProfileImage;

    private GeoPoint currentLocation;

    private GeoPoint startLocation;
    private String startAddress;
    private GeoPoint endLocation;
    private String endAddress;
    private VehicleType vehicleType;

    private LocalDateTime startTime;

    private Boolean panicActivated;
    private LocalDateTime panicActivatedAt;

    private List<PassengerDTO> passengers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PassengerDTO {
        private String firstName;
        private String lastName;
        private String email;
    }
}