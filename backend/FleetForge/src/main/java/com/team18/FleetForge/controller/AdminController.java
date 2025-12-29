package com.team18.FleetForge.controller;


import com.team18.FleetForge.dto.RouteDTO;
import com.team18.FleetForge.dto.UserSummaryDTO;
import com.team18.FleetForge.dto.admin.*;
import com.team18.FleetForge.dto.ride.view.AdminRideHistoryItemDTO;
import com.team18.FleetForge.dto.ride.view.RideDetailsDTO;
import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    @GetMapping("/{id}")
    public ResponseEntity<AdminGetResponseDTO> getAdmin(@PathVariable Long id) {
        Admin foundAdmin=new Admin();
        foundAdmin.setId(id);
        foundAdmin.setFirstName("Admin");
        foundAdmin.setLastName("Admin");
        foundAdmin.setPassword("admin");
        foundAdmin.setEmail("admin");
        foundAdmin.setPhoneNumber("123456789");
        foundAdmin.setAddress("address");
        foundAdmin.setProfilePicture("profilePicture");

        AdminGetResponseDTO adminGetResponseDTO = new AdminGetResponseDTO(foundAdmin);

        return new ResponseEntity<>(adminGetResponseDTO, HttpStatus.OK);
    }
    @PutMapping("/update-admin/{id}")
    public ResponseEntity<AdminChangeInformationResponseDTO> changeUser
            (@RequestBody AdminChangeInformationRequestDTO adminChangeInformationRequestDTO, @PathVariable String id) {
        //prvo ga posle trazim preko id i postavim nove podatke
        Admin adminToChange = Admin.builder()
                .firstName(adminChangeInformationRequestDTO.getFirstName())
                .lastName(adminChangeInformationRequestDTO.getLastName())
                .email(adminChangeInformationRequestDTO.getEmail())
                .phoneNumber(adminChangeInformationRequestDTO.getPhoneNumber())
                .address(adminChangeInformationRequestDTO.getAddress())
                .profilePicture(adminChangeInformationRequestDTO.getProfilePicture())
                .build();

        AdminChangeInformationResponseDTO adminChanged = new AdminChangeInformationResponseDTO(adminToChange);
        return new ResponseEntity<>(adminChanged, HttpStatus.OK);

    }
    @PutMapping("/{requestId}/accept-driver")
    public ResponseEntity<AdminProfileChangeStatusResponseDTO> driverChangeAccept
            (@PathVariable Long requestId) {
            //pronadje se po id driverprofilechangerequest preko requestId-a i stavi accepted na status i azurira updatedAt
        DriverProfileChangeRequest foundReq=new DriverProfileChangeRequest();
        foundReq.setNewFirstName("Novo Ime");
        foundReq.setNewLastName("Novo Prezime");
        foundReq.setNewEmail("Novi email");
        foundReq.setNewPhoneNumber("123456789");
        foundReq.setNewAddress("Nova Adresa");
        foundReq.setNewProfilePicture("profilePicture");
        foundReq.setStatus(InformationChangeRequestStatus.APPROVED);
        foundReq.setUpdatedAt(LocalDateTime.now());
        //pronaci drivera moze sad i preko ida jer sad postavljen
        Driver foundDriver= new Driver();
        foundDriver.setId(1L);

        foundDriver.setFirstName(foundReq.getNewFirstName());
        foundDriver.setLastName(foundReq.getNewLastName());
        foundDriver.setEmail(foundReq.getNewEmail());
        foundDriver.setPhoneNumber(foundReq.getNewPhoneNumber());
        foundDriver.setAddress(foundReq.getNewAddress());
        foundDriver.setProfilePicture(foundReq.getNewProfilePicture());

        // sacuvati drivera
        AdminProfileChangeStatusResponseDTO adminProfileChangeStatusResponseDTO =new AdminProfileChangeStatusResponseDTO();
        adminProfileChangeStatusResponseDTO.setDriverId(foundDriver.getId());
        adminProfileChangeStatusResponseDTO.setStatus(foundReq.getStatus());

        return new ResponseEntity<>(adminProfileChangeStatusResponseDTO,HttpStatus.OK);
    }


    @PutMapping("/{requestId}/reject-driver")
    public ResponseEntity<AdminProfileChangeStatusResponseDTO> driverChangeReject
            (@PathVariable Long requestId) {
        //pronadje se po id driverprofilechangerequest preko requestId-a i stavi accepted na status i azurira updatedAt
        DriverProfileChangeRequest foundReq=new DriverProfileChangeRequest();
        foundReq.setStatus(InformationChangeRequestStatus.REJECTED);
        foundReq.setUpdatedAt(LocalDateTime.now());
        //pronaci drivera moze sad i preko ida jer sad postavljen
        Driver foundDriver= new Driver();
        foundDriver.setId(1L);


        // sacuvati drivera
        AdminProfileChangeStatusResponseDTO adminProfileChangeStatusResponseDTO =new AdminProfileChangeStatusResponseDTO();
        adminProfileChangeStatusResponseDTO.setDriverId(foundDriver.getId());
        adminProfileChangeStatusResponseDTO.setStatus(foundReq.getStatus());

        return new ResponseEntity<>(adminProfileChangeStatusResponseDTO,HttpStatus.OK);
    }

    @GetMapping("/profile-change-requests")
    public ResponseEntity<List<AdminViewProfileChangeRequestDTO>> getPendingRequests(
            @RequestParam("status")String status) {

        AdminViewProfileChangeRequestDTO adminViewProfileChangeRequestDTO=new AdminViewProfileChangeRequestDTO();
        adminViewProfileChangeRequestDTO.setStatus(InformationChangeRequestStatus.PENDING);
        adminViewProfileChangeRequestDTO.setDriverId(1L);
        adminViewProfileChangeRequestDTO.setRequestId(1L);
        adminViewProfileChangeRequestDTO.setNewFirstName("ime");
        adminViewProfileChangeRequestDTO.setNewLastName("prezime");
        adminViewProfileChangeRequestDTO.setCreatedAt(LocalDateTime.now());
        adminViewProfileChangeRequestDTO.setDriverId(1L);
        adminViewProfileChangeRequestDTO.setNewAddress("adresa1");
        adminViewProfileChangeRequestDTO.setNewEmail("email1");
        adminViewProfileChangeRequestDTO.setNewPhoneNumber("4324324");
        adminViewProfileChangeRequestDTO.setNewProfilePicture("url1");

        adminViewProfileChangeRequestDTO.setFirstName("ime");
        adminViewProfileChangeRequestDTO.setLastName("prezime");
        adminViewProfileChangeRequestDTO.setEmail("email1");
        adminViewProfileChangeRequestDTO.setPhoneNumber("4324324");
        adminViewProfileChangeRequestDTO.setAddress("adresa1");
        adminViewProfileChangeRequestDTO.setProfilePicture("url1");

        List<AdminViewProfileChangeRequestDTO> adminViewProfileChangeRequestDTOList=new ArrayList<>();
        adminViewProfileChangeRequestDTOList.add(adminViewProfileChangeRequestDTO);

        return new ResponseEntity<>(adminViewProfileChangeRequestDTOList, HttpStatus.OK);



    }


    @PutMapping("/{requestId}/accept-vehicle")
    public ResponseEntity<AdminVehicleChangeStatusResponseDTO> vehicleChangeAccept
            (@PathVariable Long requestId) {
        //pronadje se po id driverprofilechangerequest preko requestId-a i stavi accepted na status i azurira updatedAt
        VehicleInformationChangeRequest foundReq=new VehicleInformationChangeRequest();
        foundReq.setNewModel("model");
        foundReq.setNewRegistrationNumber("registrationNumber");
        foundReq.setNewType(VehicleType.VAN);
        foundReq.setNewSpace(3);
        foundReq.setNewBabySeat(true);
        foundReq.setNewPetFriendly(true);
        foundReq.setStatus(InformationChangeRequestStatus.APPROVED);
        foundReq.setUpdatedAt(LocalDateTime.now());
        //pronaci drivera moze sad i preko ida jer sad postavljen
        Vehicle foundVehicle= new Vehicle();
        foundVehicle.setId(1L);

        foundVehicle.setModel(foundReq.getNewModel());
        foundVehicle.setRegistrationNumber(foundReq.getNewRegistrationNumber());
        foundVehicle.setType(foundReq.getNewType());
        foundVehicle.setSpace(foundReq.getNewSpace());
        foundVehicle.setBabySeat(foundReq.isNewBabySeat());
        foundVehicle.setPetFriendly(foundReq.isNewPetFriendly());


        // sacuvati drivera
        AdminVehicleChangeStatusResponseDTO adminVehicleChangeStatusResponseDTO =new AdminVehicleChangeStatusResponseDTO();
        adminVehicleChangeStatusResponseDTO.setVehicleId(foundVehicle.getId());
        adminVehicleChangeStatusResponseDTO.setStatus(foundReq.getStatus());

        return new ResponseEntity<>(adminVehicleChangeStatusResponseDTO,HttpStatus.OK);
    }

    @PutMapping("/{requestId}/reject-vehicle")
    public ResponseEntity<AdminVehicleChangeStatusResponseDTO> vehicleChangeReject
            (@PathVariable Long requestId) {
        //pronadje se po id vehicleinfochangereq preko requestId-a i stavi accepted na status i azurira updatedAt
        VehicleInformationChangeRequest foundReq=new VehicleInformationChangeRequest();
        foundReq.setStatus(InformationChangeRequestStatus.REJECTED);
        foundReq.setUpdatedAt(LocalDateTime.now());
        //pronaci vehicle moze sad i preko ida jer sad postavljen
        Vehicle foundVehicle = new Vehicle();
        foundVehicle.setId(1L);


        // sacuvati drivera
        AdminVehicleChangeStatusResponseDTO adminVehicleChangeStatusResponseDTO =new AdminVehicleChangeStatusResponseDTO();
        adminVehicleChangeStatusResponseDTO.setVehicleId(foundVehicle.getId());
        adminVehicleChangeStatusResponseDTO.setStatus(foundReq.getStatus());

        return new ResponseEntity<>(adminVehicleChangeStatusResponseDTO,HttpStatus.OK);
    }
    @PutMapping("/{id}/password-change")
    public ResponseEntity<String> passwordChange(@PathVariable Long id,@RequestBody AdminPasswordChangeRequestDTO request){
        //pronaci admina prvo preko id-a
        Admin foundAdmin=new Admin();
        foundAdmin.setPassword(request.getNewPassword());

        return ResponseEntity.ok("password changed");
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


