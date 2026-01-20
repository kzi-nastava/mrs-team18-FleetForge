package com.team18.FleetForge.controller;


import com.team18.FleetForge.dto.RouteDTO;
import com.team18.FleetForge.dto.UserSummaryDTO;
import com.team18.FleetForge.dto.admin.*;
import com.team18.FleetForge.dto.passenger.PassengerGetResponseDTO;
import com.team18.FleetForge.dto.ride.view.AdminRideHistoryItemDTO;
import com.team18.FleetForge.dto.ride.view.RideDetailsDTO;
import com.team18.FleetForge.dto.vehicle.VehicleChangeInformationResponseDTO;
import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final DriverProfileChangeRequestService driverChangeRequestService;
    private final VehicleInfoChangeReqService vehicleChangeService;
    private final VehicleService vehicleService;
    private final ProfilePictureService profilePictureService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<AdminGetResponseDTO> getCurrentAdmin(){
        Admin admin = userService.getCurrentAdmin();
        if(admin == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AdminGetResponseDTO response = new AdminGetResponseDTO(admin);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<AdminChangeInformationResponseDTO> changeCurrentAdmin(@RequestBody AdminChangeInformationRequestDTO adminChangeInformationRequestDTO) throws IOException {
        Admin admin=userService.getCurrentAdmin();
        if(admin==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        admin.setFirstName(adminChangeInformationRequestDTO.getFirstName());
        admin.setLastName(adminChangeInformationRequestDTO.getLastName());
        admin.setEmail(adminChangeInformationRequestDTO.getEmail());
        admin.setAddress(adminChangeInformationRequestDTO.getAddress());
        admin.setPhoneNumber(adminChangeInformationRequestDTO.getPhoneNumber());

        userService.save(admin);
        AdminChangeInformationResponseDTO adminChangeInformationResponseDTO = new AdminChangeInformationResponseDTO(admin);
        return ResponseEntity.ok(adminChangeInformationResponseDTO);

    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminGetResponseDTO> getAdmin(@PathVariable Long id) {
        Admin foundAdmin=(Admin) userService.getUserById(id);
        AdminGetResponseDTO adminGetResponseDTO = new AdminGetResponseDTO(foundAdmin);
        return new ResponseEntity<>(adminGetResponseDTO, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<AdminChangeInformationResponseDTO> changeUser
            (@RequestBody AdminChangeInformationRequestDTO adminChangeInformationRequestDTO, @PathVariable Long id) throws IOException {
        Admin admin=(Admin) userService.getUserById(id);
        if(admin==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        admin.setFirstName(adminChangeInformationRequestDTO.getFirstName());
        admin.setLastName(adminChangeInformationRequestDTO.getLastName());
        admin.setEmail(adminChangeInformationRequestDTO.getEmail());
        admin.setAddress(adminChangeInformationRequestDTO.getAddress());
        admin.setPhoneNumber(adminChangeInformationRequestDTO.getPhoneNumber());
        userService.save(admin);
        AdminChangeInformationResponseDTO adminChanged = new AdminChangeInformationResponseDTO(admin);
        return new ResponseEntity<>(adminChanged, HttpStatus.OK);

    }
    @Transactional
    @PutMapping("/{requestId}/driver-info")
    public ResponseEntity<AdminDriverVehicleChangeStatusResponseDTO> driverInfoChange
            (@PathVariable Long requestId,@RequestBody AdminDriverVehicleInfoChangeDTO adminDriverVehicleInfoChangeDTO) {
            DriverProfileChangeRequest request=driverChangeRequestService.findById(requestId);
            request.setUpdatedAt(LocalDateTime.now());
            AdminDriverVehicleChangeStatusResponseDTO responseDTO=new AdminDriverVehicleChangeStatusResponseDTO();

        if(adminDriverVehicleInfoChangeDTO.isChange()) {
            Driver driverToChange= (Driver)userService.getUserById(request.getDriver().getId());
            driverToChange.setFirstName(request.getNewFirstName());
            driverToChange.setLastName(request.getNewLastName());
            driverToChange.setEmail(request.getNewEmail());
            driverToChange.setAddress(request.getNewAddress());
            driverToChange.setPhoneNumber(request.getNewPhoneNumber());

            userService.save(driverToChange);

            request.setStatus(InformationChangeRequestStatus.APPROVED);
            responseDTO.setStatus(InformationChangeRequestStatus.APPROVED);
            responseDTO.setId(requestId);
            return  ResponseEntity.ok(responseDTO);
        }
        else{
            request.setStatus(InformationChangeRequestStatus.REJECTED);
            responseDTO.setStatus(InformationChangeRequestStatus.REJECTED);
            responseDTO.setId(requestId);
            return  ResponseEntity.ok(responseDTO);
        }
    }



    @GetMapping("/profile-change-requests")
    public ResponseEntity<List<AdminViewProfileChangeRequestDTO>> getPendingRequests() {


        List<DriverProfileChangeRequest> requests=driverChangeRequestService.findAllPending();
        List<AdminViewProfileChangeRequestDTO> responseDTOs=new ArrayList<>();
        for(DriverProfileChangeRequest request:requests){
            AdminViewProfileChangeRequestDTO responseDTO=new AdminViewProfileChangeRequestDTO(request,request.getDriver());
            responseDTOs.add(responseDTO);
        }

        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);

    }

@Transactional
    @PutMapping("/{requestId}/vehicle-info")
    public ResponseEntity<AdminDriverVehicleChangeStatusResponseDTO> vehicleInfoChange
            (@PathVariable Long requestId,@RequestBody  AdminDriverVehicleInfoChangeDTO adminDriverVehicleInfoChangeDTO) {
        VehicleInformationChangeRequest request=vehicleChangeService.findById(requestId);
        request.setUpdatedAt(LocalDateTime.now());
        AdminDriverVehicleChangeStatusResponseDTO responseDTO=new AdminDriverVehicleChangeStatusResponseDTO();
        if(adminDriverVehicleInfoChangeDTO.isChange()) {
            Vehicle vehicleToChange= vehicleService.findById(request.getVehicle().getId());
            vehicleToChange.setModel(request.getNewModel());
            vehicleToChange.setBabySeat(request.isNewBabySeat());
            vehicleToChange.setSpace(request.getNewSpace());
            vehicleToChange.setType(request.getNewType());
            vehicleToChange.setRegistrationNumber(request.getNewRegistrationNumber());
            vehicleToChange.setPetFriendly(request.isNewPetFriendly());
            vehicleService.save(vehicleToChange);
            request.setStatus(InformationChangeRequestStatus.APPROVED);
            responseDTO.setStatus(InformationChangeRequestStatus.APPROVED);
            responseDTO.setId(requestId);


            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        }
        else{
            request.setStatus(InformationChangeRequestStatus.REJECTED);
            responseDTO.setStatus(InformationChangeRequestStatus.REJECTED);
            responseDTO.setId(requestId);


        return new ResponseEntity<>(responseDTO,HttpStatus.OK);
        }
    }
    @GetMapping("/vehicle-change-requests")
    public ResponseEntity<List<VehicleChangeInformationResponseDTO>> getVehicleChangeRequests() {
        List<VehicleInformationChangeRequest> requests=vehicleChangeService.findAllPending();
        List<VehicleChangeInformationResponseDTO> responseDTOs=new ArrayList<>();
        for(VehicleInformationChangeRequest request:requests){
            Driver driver=vehicleChangeService.getDriverForVehicleRequest(request.getVehicle().getId());
            VehicleChangeInformationResponseDTO responseDTO=new VehicleChangeInformationResponseDTO(request,request.getVehicle(),driver);
            responseDTOs.add(responseDTO);

        }
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    @PutMapping("password")
    public ResponseEntity<Void> passwordChange(@RequestBody AdminPasswordChangeRequestDTO request){
       Admin admin=userService.getCurrentAdmin();
       if(admin==null){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
       admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
       userService.save(admin);
       return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * GET /api/admin/users/{userId}/rides
     * Get ride history for a specific user (passenger or driver)
     * Query params:
     *  - from, to (date range)
     *  - sortBy (any field)
     *  - direction (asc, desc)
     */
    @GetMapping(
            value = "/users/{userId}/rides",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<AdminRideHistoryItemDTO>> getUserRides(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ){
        List<AdminRideHistoryItemDTO> history = List.of(
                AdminRideHistoryItemDTO.builder()
                        .rideId(1L)
                        .startTime(LocalDateTime.now().minusDays(1))
                        .endTime(LocalDateTime.now().minusDays(1).plusMinutes(18))
                        .startAddress("Bulevar Oslobodjenja 1")
                        .destinationAddress("Zmaj Jovina 12")
                        .driver(UserSummaryDTO.builder()
                                .id(10L)
                                .firstName("Marko")
                                .lastName("Markovic")
                                .build())
                        .passenger(UserSummaryDTO.builder()
                                .id(20L)
                                .firstName("Petar")
                                .lastName("Petrovic")
                                .build())
                        .build(),

                AdminRideHistoryItemDTO.builder()
                        .rideId(2L)
                        .startTime(LocalDateTime.now().minusHours(2))
                        .endTime(LocalDateTime.now().minusHours(1))
                        .startAddress("Futoška 10")
                        .destinationAddress("Dunavska 5")
                        .driver(UserSummaryDTO.builder()
                                .id(11L)
                                .firstName("Jovan")
                                .lastName("Jovanovic")
                                .build())
                        .passenger(UserSummaryDTO.builder()
                                .id(21L)
                                .firstName("Ana")
                                .lastName("Anic")
                                .build())
                        .build()
        );

        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    /**
     * GET /api/admin/rides/{rideId}
     * Detailed ride view
     */
    @GetMapping(
            value = "/rides/{rideId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDetailsDTO> getRideDetailsById(
            @PathVariable Long rideId
    ) {
        RideDetailsDTO response = RideDetailsDTO.builder()
                .rideId(rideId)
                .route(RouteDTO.builder()
                        .geometry(List.of(
                                new GeoPoint(45.2671, 19.8335),
                                new GeoPoint(45.2685, 19.8400),
                                new GeoPoint(45.2700, 19.8500)
                        ))
                        .distanceMeters(5100)
                        .durationSeconds(890)
                        .build())
                .cancelled(false)
                .cancelledBy(null)
                .price(820.0)
                .panicTriggered(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


