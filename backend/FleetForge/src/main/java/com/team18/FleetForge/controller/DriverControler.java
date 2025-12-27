package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.admin.AdminPasswordChangeRequestDTO;
import com.team18.FleetForge.dto.driver.*;
import com.team18.FleetForge.dto.vehicle.VehicleCreateResponseDTO;
import com.team18.FleetForge.dto.vehicle.VehicleInformationChangeRequestDTO;
import com.team18.FleetForge.dto.vehicle.VehicleInformationChangeResponseDTO;
import com.team18.FleetForge.model.DriverProfileChangeRequest;
import com.team18.FleetForge.model.DriverSession;
import com.team18.FleetForge.model.Users.Admin;
import com.team18.FleetForge.model.Users.Driver;
import com.team18.FleetForge.model.Vehicle;
import com.team18.FleetForge.model.VehicleInformationChangeRequest;
import com.team18.FleetForge.model.enums.InformationChangeRequestStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        driverProfileChangeRequest.setStatus(InformationChangeRequestStatus.PENDING);
    // treba sacuvati ovo posle


        DriverProfileChangeResponseDTO driverProfileChangeResponseDTO = new DriverProfileChangeResponseDTO(driverProfileChangeRequest);
        return new ResponseEntity<>(driverProfileChangeResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/online")
    public ResponseEntity<DriverSessionResponseDTO> startSession(@PathVariable Long id){
        DriverSession newSession = new DriverSession();
        newSession.setDriverId(id);
        newSession.setStartedAt(LocalDateTime.now());
        //sacuvaj

        DriverSessionResponseDTO driverSessionResponseDTO = new DriverSessionResponseDTO();
       // driverSessionResponseDTO.setSessionId(newSession.getId()); //moze posle sa bazom kad se kljuc generise
        driverSessionResponseDTO.setDriverId(id);
        driverSessionResponseDTO.setStartedAt(newSession.getStartedAt());
        driverSessionResponseDTO.setActive(true);
        return new ResponseEntity<>(driverSessionResponseDTO, HttpStatus.OK);


    }

    @PutMapping("/{id}/offline")
    public ResponseEntity<DriverSessionResponseDTO> stopSession(@PathVariable Long id, @RequestBody DriverSessionEndRequestDTO request){
        //preko session id iz request.getId() nadjem session
        Long sessionId = request.getSessionId();
        DriverSession foundSession=new DriverSession();
        foundSession.setDriverId(id);
        foundSession.setStartedAt(LocalDateTime.now()); //samo za prikaz je .now inace se uzima iz ucitane sesije
        foundSession.setEndedAt(LocalDateTime.now());
        //sacuvam
        DriverSessionResponseDTO driverSessionResponseDTO = new DriverSessionResponseDTO();
        driverSessionResponseDTO.setDriverId(id);
        driverSessionResponseDTO.setStartedAt(foundSession.getStartedAt());
        driverSessionResponseDTO.setEndedAt(foundSession.getEndedAt());
        driverSessionResponseDTO.setActive(false);
        return new ResponseEntity<>(driverSessionResponseDTO, HttpStatus.OK);
    }
    @GetMapping("/{id}/active-hours")
    public ResponseEntity<DriverActivityResponseDTO> getActiveHours(@PathVariable Long id) {

        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);


        List<DriverSession> sessions = new ArrayList<>();
        DriverSession session=new DriverSession();
        session.setStartedAt(LocalDateTime.of(2025, 12, 26, 14, 30, 0));
        session.setEndedAt(LocalDateTime.of(2025, 12, 26, 19, 30, 0));
        sessions.add(session);
        // sessions = sessionRepository.findByDriverIdAndStartedAtAfter(id, last24Hours);

        Long totalHours = calculateTotalHours(sessions);

        DriverActivityResponseDTO response = new DriverActivityResponseDTO();
        response.setDriverId(id);
        response.setActiveMinutesLast24h(totalHours);

        return ResponseEntity.ok(response);
    }
    private Long calculateTotalHours(List<DriverSession> sessions) {
        return sessions.stream()
                .filter(s -> s.getEndedAt() != null)
                .mapToLong(s -> {
                    Duration duration = Duration.between(s.getStartedAt(), s.getEndedAt());
                    return duration.toMinutes() ;
                })
                .sum();
    }

    @PostMapping("/update-request-vehicle")
    public ResponseEntity<VehicleInformationChangeResponseDTO> createChangeRequest(@RequestBody VehicleInformationChangeRequestDTO request){
        //treba pronaci drivera preko maila iz baze da bi id postavio id, sad je samo privremeno ovako
        Vehicle foundVehicle=new Vehicle();
        foundVehicle.setId(1L);

        VehicleInformationChangeRequest vehicleInformationChangeRequest = new VehicleInformationChangeRequest();
        vehicleInformationChangeRequest.setVehicleId(foundVehicle.getId());
        vehicleInformationChangeRequest.setNewModel(request.getNewModel());
        vehicleInformationChangeRequest.setNewRegistrationNumber(request.getNewRegistrationNumber());
        vehicleInformationChangeRequest.setNewType(request.getNewType());
        vehicleInformationChangeRequest.setNewSpace(request.getNewSpace());
        vehicleInformationChangeRequest.setNewBabySeat(request.isNewBabySeat());
        vehicleInformationChangeRequest.setNewPetFriendly(request.isNewPetFriendly());

        vehicleInformationChangeRequest.setCreatedAt(LocalDateTime.now());
        vehicleInformationChangeRequest.setStatus(InformationChangeRequestStatus.PENDING);
        // treba sacuvati ovo posle


        VehicleInformationChangeResponseDTO vehicleInformationChangeResponseDTO = new VehicleInformationChangeResponseDTO(vehicleInformationChangeRequest);
        return new ResponseEntity<>(vehicleInformationChangeResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/password-change")
    public ResponseEntity<String> passwordChange(@PathVariable Long id,@RequestBody DriverPasswordChangeRequestDTO request){
        //pronaci drivera prvo preko id-a
        Driver foundDriver=new Driver();
        foundDriver.setPassword(request.getNewPassword());

        return ResponseEntity.ok("password changed");
    }
}
