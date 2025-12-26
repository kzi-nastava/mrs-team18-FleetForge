package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/driver")
public class DriverControler {

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


}
