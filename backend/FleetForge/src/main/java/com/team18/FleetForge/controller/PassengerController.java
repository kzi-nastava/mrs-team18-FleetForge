package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.passenger.PassengerChangeInformationRequestDTO;
import com.team18.FleetForge.dto.passenger.PassengerChangeInformationResponseDTO;
import com.team18.FleetForge.dto.passenger.PassengerGetResponseDTO;
import com.team18.FleetForge.dto.passenger.PassengerPasswordChangeRequestDTO;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PassengerGetResponseDTO> getCurrentPassenger() {
        Passenger passenger = userService.getCurrentPassenger();
        if(passenger == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        PassengerGetResponseDTO response = new PassengerGetResponseDTO(passenger);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<PassengerChangeInformationResponseDTO> changeCurrentPassenger(@RequestBody PassengerChangeInformationRequestDTO passengerChangeInformationRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Passenger currentPassenger =userService.getCurrentPassenger();
        if(currentPassenger == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        currentPassenger.setEmail(passengerChangeInformationRequestDTO.getEmail());
        currentPassenger.setFirstName(passengerChangeInformationRequestDTO.getFirstName());
        currentPassenger.setLastName(passengerChangeInformationRequestDTO.getLastName());
        currentPassenger.setAddress(passengerChangeInformationRequestDTO.getAddress());
        currentPassenger.setPhoneNumber(String.valueOf(passengerChangeInformationRequestDTO.getPhoneNumber()));
        currentPassenger.setProfilePicture(passengerChangeInformationRequestDTO.getProfilePicture());
        userService.save(currentPassenger);

        PassengerChangeInformationResponseDTO passengerChanged = new PassengerChangeInformationResponseDTO(currentPassenger);
        return new ResponseEntity<>(passengerChanged, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerGetResponseDTO> getPassenger(@PathVariable Long id) {
        Passenger foundPassenger =(Passenger) userService.getUserById(id);
        PassengerGetResponseDTO passengerGetResponseDTO = new PassengerGetResponseDTO(foundPassenger);
        return new ResponseEntity<>(passengerGetResponseDTO, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<PassengerChangeInformationResponseDTO> changeUser
            (@RequestBody PassengerChangeInformationRequestDTO passengerChangeInformationRequestDTO, @PathVariable Long id) {
        Passenger foundPassenger =(Passenger) userService.getUserById(id);
        foundPassenger.setFirstName(passengerChangeInformationRequestDTO.getFirstName());
        foundPassenger.setLastName(passengerChangeInformationRequestDTO.getLastName());
        foundPassenger.setEmail(passengerChangeInformationRequestDTO.getEmail());
        foundPassenger.setAddress(passengerChangeInformationRequestDTO.getAddress());
        foundPassenger.setPhoneNumber(String.valueOf(passengerChangeInformationRequestDTO.getPhoneNumber()));
        foundPassenger.setProfilePicture(passengerChangeInformationRequestDTO.getProfilePicture());

        userService.save(foundPassenger);

        PassengerChangeInformationResponseDTO passengerChanged = new PassengerChangeInformationResponseDTO(foundPassenger);
        return new ResponseEntity<>(passengerChanged, HttpStatus.OK);
    }

    @PutMapping("password")
    public ResponseEntity<String> passwordChange(@RequestBody PassengerPasswordChangeRequestDTO request){
        Passenger passenger = userService.getCurrentPassenger();
        if(passenger == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        passenger.setPassword(request.getNewPassword());
        return ResponseEntity.ok("password changed");
    }
}