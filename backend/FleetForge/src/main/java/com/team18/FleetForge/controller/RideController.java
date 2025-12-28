package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.ride.lifecycle.*;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.routes.FavoriteRouteGetResponseDTO;
import com.team18.FleetForge.dto.ride.routes.FavoriteRoutePostDeleteRequestDTO;
import com.team18.FleetForge.dto.ride.routes.FavoriteRoutePostDeleteResponseDTO;
import com.team18.FleetForge.dto.ride.review.RideReviewRequestDTO;
import com.team18.FleetForge.dto.ride.review.RideReviewResponseDTO;
import com.team18.FleetForge.dto.ride.view.RideTrackingDTO;
import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Ride;
import com.team18.FleetForge.model.Route;
import com.team18.FleetForge.model.enums.RideCancellationRole;
import com.team18.FleetForge.model.enums.RideStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
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
//    Authentication authentication = authenticationManager.authenticate(
//            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
//    );
//
//SecurityContextHolder.getContext().setAuthentication(authentication);

    @GetMapping("/favorites")
    public ResponseEntity<FavoriteRouteGetResponseDTO> getFavouriteRoutes(Principal principal) {
        //trazim rute iz passengera a njega iz principal/authentication-a
//principal.getId ili slicno

        ArrayList<Route> routes = new ArrayList<>();
        Route route = new Route();
        Route route2 = new Route();
        GeoPoint g1=new GeoPoint(45.2671, 19.8335);
        GeoPoint g2=new GeoPoint(45.2671, 19.8335);
        route.setGeometry(new ArrayList<GeoPoint>());
        route.getGeometry().add(g1);
        route.getGeometry().add(g2);
        route.setDistanceMeters(2500);
        route.setDurationSeconds(25000);
        route.setId(1L);

        GeoPoint g3=new GeoPoint(45.2671, 19.8335);
        GeoPoint g4=new GeoPoint(45.2671, 19.8335);
        route2.setGeometry(new ArrayList<>());
        route2.getGeometry().add(g3);
        route2.getGeometry().add(g4);
        route2.setDistanceMeters(3000);
        route2.setDurationSeconds(30000);
        route2.setId(2L);
        routes.add(route);
        routes.add(route2);



        FavoriteRouteGetResponseDTO responseDTO=new FavoriteRouteGetResponseDTO();
        responseDTO.setRoutes(routes);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteRoutePostDeleteRequestDTO> addFavoriteRoute(@RequestBody FavoriteRoutePostDeleteRequestDTO request) {

        //naci putnika preko principal/authenticationa i dodati mu rutu iz request u listu omiljenih
        FavoriteRoutePostDeleteRequestDTO responseDTO = new FavoriteRoutePostDeleteRequestDTO();
        responseDTO.setRoute(new Route());
        responseDTO.setRoute(request.getRoute());
        return  new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/favorites/{routeId}")
    public ResponseEntity<FavoriteRoutePostDeleteResponseDTO> deleteFavoriteRoute(@PathVariable Long routeId) {
        //naci rutu preko id-a iz passengera ciji su podaci sacuvani jer je ulogovan
        Route route = new Route();
        route.setId(routeId);
        FavoriteRoutePostDeleteResponseDTO responseDTO = new FavoriteRoutePostDeleteResponseDTO();
        responseDTO.setRoute(route);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    /**
     * PUT /api/rides/{rideId}/complete
     * Response:
     *  - rideId (Long)
     *  - status (String) - "COMPLETED"
     *  - completedAt (LocalDateTime)
     *  - finalPrice (Double)
     *  - message (String)
     *  - nextRide (NextRideDTO, optional) - if driver has next scheduled ride
     */
    @PutMapping(
            value = "/{rideId}/complete",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideCompletionResponseDTO> completeRide(@PathVariable Long rideId) {
        // Dummy completion data with next scheduled ride
        RideCompletionResponseDTO response = RideCompletionResponseDTO.builder()
                .rideId(rideId)
                .status("COMPLETED")
                .completedAt(LocalDateTime.now())
                .finalPrice(1450.00)
                .message("Ride completed successfully. Driver is now available.")
                .nextRide(RideCompletionResponseDTO.NextRideDTO.builder()
                        .rideId(456L)
                        .startLocation(new GeoPoint(45.2550, 19.8450))
                        .startAddress("Bulevar oslobođenja 46, Novi Sad")
                        .endLocation(new GeoPoint(45.2671, 19.8335))
                        .endAddress("Trg slobode 1, Novi Sad")
                        .scheduledFor(LocalDateTime.now().plusMinutes(30))
                        .estimatedDurationMinutes(15)
                        .passenger(RideCompletionResponseDTO.NextRideDTO.PassengerInfoDTO.builder()
                                .id(25L)
                                .firstName("Ana")
                                .lastName("Anić")
                                .phoneNumber("+381649876543")
                                .profileImage("passenger25.jpg")
                                .build())
                        .build())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /api/rides/{rideId}/review
     * Request Body:
     *  - vehicleRating (Integer, 1-5, required)
     *  - driverRating (Integer, 1-5, required)
     *  - comment (String, optional, max 500 chars)
     * Response:
     *  - rideId (Long)
     *  - vehicleRating (Integer)
     *  - driverRating (Integer)
     *  - comment (String)
     *  - reviewedAt (LocalDateTime)
     *  - message (String)
     */
    @PostMapping(
            value = "/{rideId}/review",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideReviewResponseDTO> submitReview(
            @PathVariable Long rideId,
            @Valid @RequestBody RideReviewRequestDTO request) {

        // Dummy review response
        RideReviewResponseDTO response = RideReviewResponseDTO.builder()
                .rideId(rideId)
                .vehicleRating(request.getVehicleRating())
                .driverRating(request.getDriverRating())
                .comment(request.getComment())
                .reviewedAt(LocalDateTime.now())
                .message("Review submitted successfully. Thank you for your feedback!")
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("/{id}/start")
    public ResponseEntity<RideStartResponseDTO> startRide(@PathVariable Long id) {
        RideStartResponseDTO responseDTO = new RideStartResponseDTO();
        responseDTO.setId(id);
        responseDTO.setStatus(RideStatus.IN_PROGRESS);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
