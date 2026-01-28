package com.team18.FleetForge.service.rides;

import com.team18.FleetForge.dto.ride.lifecycle.RideStartResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.repository.rides.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RideStartService {

    private final RideRepository rideRepository;

    public ResponseEntity<RideStartResponseDTO> startRide(Ride ride) {
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartTime(LocalDateTime.now());
        rideRepository.save(ride);
        RideStartResponseDTO responseDTO = new RideStartResponseDTO();
        responseDTO.setStatus(ride.getStatus());
        responseDTO.setId(ride.getId());
        return ResponseEntity.ok(responseDTO);
    }
}
