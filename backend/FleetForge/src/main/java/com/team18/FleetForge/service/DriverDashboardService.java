package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.ride.lifecycle.UpcomingRideDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.DriverRepository;
import com.team18.FleetForge.repository.RideRepository;
import com.team18.FleetForge.dto.driver.DailyStatsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverDashboardService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    public List<UpcomingRideDTO> getUpcomingRides(Long id) {

        List<Ride> upcomingRides = rideRepository.findActiveRidesByDriverId(id);

        return upcomingRides.stream()
                .map(this::buildUpcomingRideDTO)
                .collect(Collectors.toList());

    }

    public DailyStatsDTO getDailyStats(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Ride> completedRidesToday = rideRepository.findAllByDriverAndStatusAndEndTimeBetween(
                driver,
                RideStatus.COMPLETED,
                startOfDay,
                endOfDay
        );

        Double totalEarnings = completedRidesToday.stream()
                .mapToDouble(Ride::getTotalCost)
                .sum();

        Double hoursWorked = 5.0; 

        Integer ridesCompleted = completedRidesToday.size();

        return DailyStatsDTO.builder()
                .totalEarnings(totalEarnings)
                .hoursWorked(hoursWorked)
                .ridesCompleted(ridesCompleted)
                .build();
    }


    private UpcomingRideDTO buildUpcomingRideDTO(Ride ride) {
        return UpcomingRideDTO.builder()
                .rideId(ride.getId())
                .passengerName(ride.getPassenger().getFirstName() + " " + ride.getPassenger().getLastName())
                .passengerImage(ride.getPassenger().getProfilePicture())
                .startAddress(ride.getStartAddress())
                .endAddress(ride.getEndAddress())
                .startTime(ride.getStartTime())
                .cost(ride.getTotalCost())
                .build();
    }
}
