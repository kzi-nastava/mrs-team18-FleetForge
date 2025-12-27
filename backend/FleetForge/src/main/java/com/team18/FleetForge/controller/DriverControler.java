package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.driver.*;
import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.Users.Driver;
import com.team18.FleetForge.model.enums.DriverProfileChangeRequestStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/driver")
public class DriverControler {

    @GetMapping("/{id}")
    public ResponseEntity<DriverGetResponseDTO> getDriver(@PathVariable Long id) {
        Driver foundDriver =new Driver();
        foundDriver.setId(id);
        foundDriver.setFirstName("Admin");
        foundDriver.setLastName("Admin");
        foundDriver.setPassword("admin");
        foundDriver.setEmail("admin");
        foundDriver.setPhoneNumber("123456789");
        foundDriver.setAddress("address");
        foundDriver.setProfilePicture("profilePicture");

        DriverGetResponseDTO driverGetResponseDTO = new DriverGetResponseDTO(foundDriver);

        return new ResponseEntity<>(driverGetResponseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DriverCreateResponseDTO> createDriver(@RequestBody DriverCreateRequestDTO request){
        DriverCreateResponseDTO driverCreateResponseDTO = new DriverCreateResponseDTO();
        driverCreateResponseDTO.setFirstName(request.getFirstName());
        driverCreateResponseDTO.setLastName(request.getLastName());
        driverCreateResponseDTO.setEmail(request.getEmail());
        driverCreateResponseDTO.setPhone(request.getPhone());
        driverCreateResponseDTO.setAddress(request.getAddress());

        VehicleCreateResponseDTO vehicleCreateResponseDTO = new VehicleCreateResponseDTO();
        vehicleCreateResponseDTO.setType(request.getVehicle().getType());
        vehicleCreateResponseDTO.setRegistrationNumber(request.getVehicle().getRegistrationNumber());
        vehicleCreateResponseDTO.setModel(request.getVehicle().getModel());
        vehicleCreateResponseDTO.setSpace(request.getVehicle().getSpace());
        vehicleCreateResponseDTO.setPetFriendly(request.getVehicle().isPetFriendly());
        vehicleCreateResponseDTO.setBabySeat(request.getVehicle().isBabySeat());

        driverCreateResponseDTO.setVehicle(vehicleCreateResponseDTO);
    //treba sacuvati sve posle u bazi
        return  new ResponseEntity<>(driverCreateResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/set-password")
    public ResponseEntity<DriverPasswordResponseDTO> setDriverPassword(@PathVariable long id, @RequestBody DriverPasswordRequestDTO request){
        DriverPasswordResponseDTO driverPasswordResponseDTO = new DriverPasswordResponseDTO();

        //ovde treba da se dobavlja iz baze preko id pa da se azurira i da se vrati nesto ovo sam lupio podatke samo sad jer nema baze
        driverPasswordResponseDTO.setId(id);
        driverPasswordResponseDTO.setEmail("milos.damjanovic@gmail.com");

        return new ResponseEntity<>(driverPasswordResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/update-request")
    public ResponseEntity<DriverProfileChangeResponseDTO> createChangeRequest(@RequestBody DriverProfileChangeRequestDTO request){
            //treba pronaci drivera preko maila iz baze da bi id postavio id, sad je samo privremeno ovako
        Driver foundDriver=new Driver();
        foundDriver.setId(1L);

        DriverProfileChangeRequest driverProfileChangeRequest = new DriverProfileChangeRequest();
        driverProfileChangeRequest.setDriverId(foundDriver.getId());
        driverProfileChangeRequest.setNewFirstName(request.getNewFirstName());
        driverProfileChangeRequest.setNewLastName(request.getNewLastName());
        driverProfileChangeRequest.setNewEmail(request.getNewEmail());
        driverProfileChangeRequest.setNewPhoneNumber(request.getNewPhoneNumber());
        driverProfileChangeRequest.setNewAddress(request.getNewAddress());
        driverProfileChangeRequest.setNewProfilePicture(request.getNewProfilePicture());
        driverProfileChangeRequest.setCreatedAt(LocalDateTime.now());
        driverProfileChangeRequest.setStatus(DriverProfileChangeRequestStatus.PENDING);
    // treba sacuvati ovo posle


        DriverProfileChangeResponseDTO driverProfileChangeResponseDTO = new DriverProfileChangeResponseDTO(driverProfileChangeRequest);
        return new ResponseEntity<>(driverProfileChangeResponseDTO, HttpStatus.CREATED);
    }
}
