package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.ride.lifecycle.RideStartResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RideStartService {

    RideRepository rideRepository;

    public ResponseEntity<RideStartResponseDTO> startRide(Ride ride) {
        ride.setStatus(RideStatus.IN_PROGRESS);
        rideRepository.save(ride);
        RideStartResponseDTO responseDTO = new RideStartResponseDTO();
        responseDTO.setStatus(ride.getStatus());
        responseDTO.setId(ride.getId());
        return ResponseEntity.ok(responseDTO);
    }
}
