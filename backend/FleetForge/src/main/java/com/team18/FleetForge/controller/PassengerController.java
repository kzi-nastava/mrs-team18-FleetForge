package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.driver.DriverGetResponseDTO;
import com.team18.FleetForge.dto.driver.DriverPasswordChangeRequestDTO;
import com.team18.FleetForge.dto.passenger.PassengerChangeInformationRequestDTO;
import com.team18.FleetForge.dto.passenger.PassengerChangeInformationResponseDTO;
import com.team18.FleetForge.dto.passenger.PassengerGetResponseDTO;
import com.team18.FleetForge.dto.passenger.PassengerPasswordChangeRequestDTO;
import com.team18.FleetForge.model.Users.Driver;
import com.team18.FleetForge.model.Users.Passenger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {

    @GetMapping("/{id}")
    public ResponseEntity<PassengerGetResponseDTO> getDriver(@PathVariable Long id) {
        Passenger foundPassenger =new Passenger();
        foundPassenger.setId(id);
        foundPassenger.setFirstName("Admin");
        foundPassenger.setLastName("Admin");
        foundPassenger.setPassword("admin");
        foundPassenger.setEmail("admin");
        foundPassenger.setPhoneNumber("123456789");
        foundPassenger.setAddress("address");
        foundPassenger.setProfilePicture("profilePicture");

        PassengerGetResponseDTO passengerGetResponseDTO = new PassengerGetResponseDTO(foundPassenger);

        return new ResponseEntity<>(passengerGetResponseDTO, HttpStatus.OK);
    }
    @PutMapping("/update-passenger/{id}")
    public ResponseEntity<PassengerChangeInformationResponseDTO> changeUser
            (@RequestBody PassengerChangeInformationRequestDTO passengerChangeInformationRequestDTO, @PathVariable String id) {
        //prvo ga posle trazim preko id i postavim nove podatke
        Passenger passengerToChange = Passenger.builder()
                .firstName(passengerChangeInformationRequestDTO.getFirstName())
                .lastName(passengerChangeInformationRequestDTO.getLastName())
                .email(passengerChangeInformationRequestDTO.getEmail())
                .phoneNumber(passengerChangeInformationRequestDTO.getPhoneNumber())
                .address(passengerChangeInformationRequestDTO.getAddress())
                .profilePicture(passengerChangeInformationRequestDTO.getProfilePicture())
                .build();

        PassengerChangeInformationResponseDTO passengerChanged = new PassengerChangeInformationResponseDTO(passengerToChange);
        return new ResponseEntity<>(passengerChanged, HttpStatus.OK);

    }

    @PutMapping("/{id}/password-change")
    public ResponseEntity<String> passwordChange(@PathVariable Long id,@RequestBody PassengerPasswordChangeRequestDTO request){
        //pronaci passengera prvo preko id-a
        Passenger foundPassenger=new Passenger();
        foundPassenger.setPassword(request.getNewPassword());

        return ResponseEntity.ok("password changed");
    }
}