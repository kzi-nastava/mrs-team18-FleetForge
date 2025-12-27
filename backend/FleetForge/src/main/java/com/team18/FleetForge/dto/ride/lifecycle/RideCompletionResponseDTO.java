package com.team18.FleetForge.dto.ride.lifecycle;

import com.team18.FleetForge.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideCompletionResponseDTO {

    private Long rideId;
    private String status; // "COMPLETED"
    private LocalDateTime completedAt;
    private Double finalPrice;
    private String message;

    // Optional: Next scheduled ride if driver has one
    private NextRideDTO nextRide;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NextRideDTO {
        private Long rideId;
        private GeoPoint startLocation;
        private String startAddress;
        private GeoPoint endLocation;
        private String endAddress;
        private LocalDateTime scheduledFor;
        private Integer estimatedDurationMinutes;
        private PassengerInfoDTO passenger;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PassengerInfoDTO {
            private Long id;
            private String firstName;
            private String lastName;
            private String phoneNumber;
            private String profileImage;
        }
    }
}