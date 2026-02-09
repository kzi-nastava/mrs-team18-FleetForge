package com.team18.FleetForge.controller.user;


import com.team18.FleetForge.dto.RouteDTO;
import com.team18.FleetForge.dto.admin.*;
import com.team18.FleetForge.dto.ride.view.AdminRideDetailsDTO;
import com.team18.FleetForge.dto.ride.view.AdminRideHistoryDTO;
import com.team18.FleetForge.dto.vehicle.VehicleChangeInformationResponseDTO;
import com.team18.FleetForge.model.users.DriverProfileChangeRequest;
import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.vecihles.Vehicle;
import com.team18.FleetForge.model.vecihles.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import com.team18.FleetForge.service.rides.RideService;
import jakarta.validation.Valid;
import com.team18.FleetForge.service.users.DriverProfileChangeRequestService;
import com.team18.FleetForge.service.users.UserService;
import com.team18.FleetForge.service.vehicles.VehicleInfoChangeReqService;
import com.team18.FleetForge.service.vehicles.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final RideService rideService;
    private final PasswordEncoder passwordEncoder;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<AdminGetResponseDTO> getCurrentAdmin(){
        Admin admin = userService.getCurrentAdmin();
        if(admin == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AdminGetResponseDTO response = new AdminGetResponseDTO(admin);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<AdminChangeInformationResponseDTO> changeCurrentAdmin(@Valid @RequestBody AdminChangeInformationRequestDTO adminChangeInformationRequestDTO) throws IOException {
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
            (@Valid @RequestBody AdminChangeInformationRequestDTO adminChangeInformationRequestDTO, @PathVariable Long id) throws IOException {
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
            (@PathVariable Long requestId,@Valid @RequestBody AdminDriverVehicleInfoChangeDTO adminDriverVehicleInfoChangeDTO) {
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


    @PreAuthorize("hasRole('ADMIN')")
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
@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{requestId}/vehicle-info")
    public ResponseEntity<AdminDriverVehicleChangeStatusResponseDTO> vehicleInfoChange
            (@PathVariable Long requestId,@Valid @RequestBody  AdminDriverVehicleInfoChangeDTO adminDriverVehicleInfoChangeDTO) {
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/password")
    public ResponseEntity<?> passwordChange(@Valid @RequestBody AdminPasswordChangeRequestDTO request){
       Admin admin=userService.getCurrentAdmin();
       if(admin==null){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
       admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
       userService.save(admin);
       return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * GET /api/admin/search-users
     * Get usernames by string prefix
     * Query params:
     *  - prefix (username prefix)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search-users")
    public ResponseEntity<List<String>> searchUsersByEmail(@RequestParam String prefix) {
        List<String> emails = userService.searchUserEmailsByPrefix(prefix);
        return ResponseEntity.ok(emails);
    }

    /**
     * GET /api/admin/rides
     * Get ride history for a specific user (passenger or driver)
     * Query params:
     *  - from, to (date range)
     *  - sortBy (any field)
     *  - direction (asc, desc)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(
            value = "/rides",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<AdminRideHistoryDTO>> getUserRideHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminRideHistoryDTO> rides = rideService.getAdminRideHistory(
                userId,
                email,
                from,
                to,
                sortBy,
                direction,
                page,
                size
        );
        return ResponseEntity.ok(rides);
    }


    /**
     * GET /api/admin/rides/{rideId}
     * Detailed ride view
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(
            value = "/rides/{rideId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AdminRideDetailsDTO> getRideDetailsById(
            @PathVariable Long rideId
    ) {
        return ResponseEntity.ok(
                rideService.getAdminRideDetails(rideId)
        );
    }

}


