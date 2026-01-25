package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.passenger.PassengerChangeInformationRequestDTO;
import com.team18.FleetForge.dto.passenger.PassengerChangeInformationResponseDTO;
import com.team18.FleetForge.dto.passenger.PassengerGetResponseDTO;
import com.team18.FleetForge.dto.passenger.PassengerPasswordChangeRequestDTO;
import com.team18.FleetForge.dto.ride.routes.FavoriteRouteGetResponseDTO;
import com.team18.FleetForge.dto.ride.routes.FavoriteRoutePostDeleteRequestDTO;
import com.team18.FleetForge.dto.ride.routes.FavoriteRoutePostDeleteResponseDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.Route;
import com.team18.FleetForge.model.ride.FavoriteRoute;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.FavoriteRouteService;
import com.team18.FleetForge.service.RideService;
import com.team18.FleetForge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/passenger")
@RequiredArgsConstructor
public class PassengerController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RideService rideService;
    private final FavoriteRouteService favoriteRouteService;

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
        passenger.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(passenger);
        return ResponseEntity.ok("password changed");
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteRouteGetResponseDTO>> getFavouriteRoutes() {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        Passenger passenger = (Passenger) authentication.getPrincipal();
        List<FavoriteRoute> favRoutes=favoriteRouteService.findByPassengerId(passenger.getId());
        List<FavoriteRouteGetResponseDTO> responseDTOList = new ArrayList<>();
        for(FavoriteRoute favRoute:favRoutes){
            FavoriteRouteGetResponseDTO responseDTO = new FavoriteRouteGetResponseDTO();
            responseDTO.setId(favRoute.getId());
            responseDTO.setStartAddress(favRoute.getRide().getStartAddress());
            responseDTO.setEndAddress(favRoute.getRide().getEndAddress());
            responseDTO.setWaypoints(favRoute.getRide().getWayPoints());
            responseDTO.setName(favRoute.getName());
            responseDTO.setRideId(favRoute.getRide().getId());
            responseDTOList.add(responseDTO);
        }
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    @PostMapping("/favorites/{routeName}/{rideId}")
    public ResponseEntity<?> addFavoriteRoute(@PathVariable Long rideId, @PathVariable String routeName) {
        Authentication  authentication = SecurityContextHolder.getContext().getAuthentication();
        Passenger passenger = (Passenger) authentication.getPrincipal();
        FavoriteRoute route=new FavoriteRoute();
        Ride ride=rideService.getRideById(rideId);
        route.setRide(ride);
        route.setName(routeName);
        route.setPassenger(passenger);
        favoriteRouteService.save(route);
        return new ResponseEntity<>( HttpStatus.OK);
    }

    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<?> deleteFavoriteRoute(@PathVariable Long id) {
        FavoriteRoute route= favoriteRouteService.findById(id);
        favoriteRouteService.delete(route);
        return new ResponseEntity<>( HttpStatus.OK);
    }
}