package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.lifecycle.*;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideGetResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideTrackingDTO;
import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Ride;
import com.team18.FleetForge.model.Route;
import com.team18.FleetForge.model.enums.RideCancellationRole;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import jakarta.validation.Valid;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    /**
     * POST /api/rides/{rideId}/cancel
     * Request Body:
     *  - cancelledBy (DRIVER | PASSENGER)
     *  - reason (String, required for DRIVER)
     *  - scheduledStartTime (LocalDateTime)
     * Response:
     *  - success (boolean)
     *  - message (String)
     */
    @PostMapping(
            value = "/{rideId}/cancel",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideCancellationResponseDTO> cancelRide(
            @PathVariable Long rideId,
            @RequestBody RideCancellationRequestDTO request
    ) {
        LocalDateTime now = LocalDateTime.now();

        if (request.getCancelledBy() == RideCancellationRole.PASSENGER) {
            long minutesUntilStart =
                    Duration.between(now, request.getScheduledStartTime()).toMinutes();

            if (minutesUntilStart < 10) {
                return new ResponseEntity<>(
                        new RideCancellationResponseDTO(
                                false,
                                "Passenger cancellation allowed only 10 minutes before ride start."
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        if (request.getCancelledBy() == RideCancellationRole.DRIVER &&
                (request.getReason() == null || request.getReason().isBlank())) {
            return new ResponseEntity<>(
                    new RideCancellationResponseDTO(
                            false,
                            "Driver must provide a cancellation reason."
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                new RideCancellationResponseDTO(
                        true,
                        "Ride " + rideId + " successfully cancelled."
                ),
                HttpStatus.OK
        );
    }

    /**
     * POST /api/rides/{rideId}/early-end
     * Request Body:
     *  - stopLocation (GeoPoint)
     *  - endTime (LocalDateTime)
     * Response:
     *  - finalDestination (GeoPoint)
     *  - finalPrice (double)
     */
    @PostMapping(
            value = "/{rideId}/early-end",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideEndResponseDTO> endRide(
            @PathVariable Long rideId,
            @RequestBody RideEndRequestDTO request
    ) {
        // Dummy recalculation logic
        double recalculatedPrice = 620.00;

        RideEndResponseDTO response = RideEndResponseDTO.builder()
                .finalDestination(request.getStopLocation())
                .finalPrice(recalculatedPrice)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /api/rides/{rideId}/tracking
     * Response:
     *  - rideId (Long)
     *  - status (String)
     *  - currentLocation (GeoPoint)
     *  - estimatedArrivalMinutes (Integer)
     *  - route (RouteInfoDTO)
     *  - driver (DriverInfoDTO)
     *  - vehicle (VehicleInfoDTO)
     *  - panicActivated (Boolean)
     */
    @GetMapping(
            value = "/{rideId}/tracking",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideTrackingDTO> getLiveRideTracking(@PathVariable Long rideId) {

        RideTrackingDTO response = RideTrackingDTO.builder()
                .rideId(rideId)
                .status("IN PROGRESS")
                .currentLocation(new GeoPoint(45.2671, 19.8335))
                .estimatedArrivalMinutes(12)
                .route(RideTrackingDTO.RouteInfoDTO.builder()
                        .startLocation(new GeoPoint(45.2550, 19.8450))
                        .startAddress("Bulevar oslobođenja 46, Novi Sad")
                        .endLocation(new GeoPoint(45.2671, 19.8335))
                        .endAddress("Trg slobode 1, Novi Sad")
                        .totalDistanceKm(5.2)
                        .build())
                .driver(DriverInfoDTO.builder()
                        .id(10L)
                        .firstName("Marko")
                        .lastName("Marković")
                        .phoneNumber("+381641234567")
                        .profileImage("driver10.jpg")
                        .build())
                .panicActivated(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /api/rides/{rideId}/report-inconsistency
     * Request Body:
     *  - comment (String)
     *  - currentLocation (GeoPoint, optional)
     * Response:
     *  - reportId (Long)
     *  - message (String)
     *  - reportedAt (LocalDateTime)
     */
    @PostMapping(
            value = "/{rideId}/report-inconsistency",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InconsistencyReportResponseDTO> reportInconsistency(
            @PathVariable Long rideId,
            @Valid @RequestBody InconsistencyReportDTO request
    ) {

        InconsistencyReportResponseDTO response = InconsistencyReportResponseDTO.builder()
                .reportId(123L)
                .message("Inconsistency report submitted successfully")
                .reportedAt(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create")
    public ResponseEntity<RideCreateResponseDTO> createRide(@RequestBody RideCreateRequestDTO request) {
        Route route = new Route();
        route.setGeometry(new ArrayList<>(request.getCoordinates()));

    double price=calculatePrice(route,request);
    //u servisu posle treba proveriti da li ima dostupnih vozaca i odraditi ostalu logiku za dodelu vozaca 
        Ride ride = Ride.builder()
                .id(1L)
                .passengerId(1L)
                .route(route)
                .passengerNumber(request.getPassengerNumber())
                .rideTime(request.getRideTime())
                .rideNow(request.isRideNow())
                .passengerEmails(request.getPassengerEmails())
                .vehicleType(request.getVehicleType())
                .babySeat(request.isBabySeat())
                .petFriendly(request.isPetFriendly())
                .panicActivated(false)
                .startTime(request.getRideTime())
                .rideStatus(RideStatus.PENDING)
                .totalPrice(price)
                .build();

        RideCreateResponseDTO response = RideCreateResponseDTO.builder()
                .rideId(1L)
                .status(RideStatus.PENDING)
                .estimatedPrice(price)
                .message("Ride created successfully")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private double calculatePrice(Route route,RideCreateRequestDTO request) {
        double km=20;
        double price=120*km;
        switch (request.getVehicleType()){
            case VAN -> price+=250;
            default -> price+=500;
        }
        return price;

    }

    @GetMapping("/{id}")
    public ResponseEntity<RideGetResponseDTO> getRide(@PathVariable Long id) {
        Route route = new Route();
        GeoPoint g1=new GeoPoint(45.2671, 19.8335);
        GeoPoint g2=new GeoPoint(45.2671, 19.8335);
        route.setGeometry(new ArrayList<GeoPoint>());
        route.getGeometry().add(g1);
        route.getGeometry().add(g2);
        ArrayList<String> passengerEmails=new ArrayList<>();
        passengerEmails.add("email1");
        passengerEmails.add("email2");

        RideGetResponseDTO responseDTO=new RideGetResponseDTO().builder()
                .id(id)
                .route(route)
                .passengerNumber(4)
                .rideTime(LocalDateTime.of(2025,3,2,15,0))
                .rideNow(false)
                .passengerEmails(passengerEmails)
                .vehicleType(VehicleType.VAN)
                .babySeat(true)
                .petFriendly(false)
                .panicActivated(false)
                .startTime(LocalDateTime.of(2025,3,2,15,5))
                .stopTime(LocalDateTime.of(2025,3,2,16,5))
                .rideStatus(RideStatus.PENDING)
                .totalPrice(2500)
                .cancellationReason("None")
                .stopLocation(new GeoPoint(45.2671, 19.8335))
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
