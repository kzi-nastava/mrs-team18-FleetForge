package com.team18.FleetForge.controller;


import com.team18.FleetForge.dto.admin.*;
import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import org.springframework.http.HttpStatus;
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
}


