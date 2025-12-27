package com.team18.FleetForge.dto.driver;

import com.team18.FleetForge.model.GeoPoint;
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
public class DriverRideHistoryDTO {

    private Long rideId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private GeoPoint startLocation;
    private String startAddress;
    private GeoPoint endLocation;
    private String endAddress;

    private Double totalPrice;

    private Boolean cancelled;
    private String cancelledBy; // "DRIVER", "PASSENGER", or null

    private Boolean panicActivated;

    private List<PassengerDTO> passengers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PassengerDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String profileImage;
    }
}