package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.dto.RouteDTO;
import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.driver.DriverRideHistoryDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCreateRequestDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.routes.WayPointDTO;
import com.team18.FleetForge.dto.ride.view.PassengerRideDetailsDTO;
import com.team18.FleetForge.dto.ride.view.RideDetailsDTO;
import com.team18.FleetForge.model.ride.*;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.rides.InconsistencyReportRepository;
import com.team18.FleetForge.repository.rides.RideLocationRepository;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import com.team18.FleetForge.service.users.DriverService;
import com.team18.FleetForge.service.PriceCalculationService;
import com.team18.FleetForge.service.rides.RideService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.team18.FleetForge.dto.ride.view.PassengerRideHistoryDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverService driverService;
    private final PriceCalculationService priceCalculationService;
    private final RideLocationRepository rideLocationRepository;
    private final InconsistencyReportRepository inconsistencyReportRepository;

    @Override
    public List<DriverRideHistoryDTO> getDriverRideHistory(Long driverId, LocalDate startDate) {
        log.info("Fetching ride history for driver ID: {} with start date: {}", driverId, startDate);

        LocalDateTime startDateTime = null;
        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        List<Ride> rides = rideRepository.findDriverRideHistory(driverId, startDate, startDateTime);

        log.info("Found {} filtered rides for driver ID: {}", rides.size(), driverId);

        return rides.stream()
                .map(this::mapToDriverRideHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverRideHistoryDTO> getAllDriverRides(Long driverId) {
        log.info("Fetching all rides for driver ID: {}", driverId);

        List<Ride> rides = rideRepository.findAllByDriverId(driverId);

        log.info("Found {} total rides for driver ID: {}", rides.size(), driverId);

        return rides.stream()
                .map(this::mapToDriverRideHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RideDetailsDTO getRideDetails(Long rideId) {
        log.info("Fetching ride details for ride ID: {}", rideId);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

        return mapToRideDetailsDTO(ride);
    }

    @Override
    public boolean hasActiveRides(Long driverId) {
        log.info("Checking if driver ID: {} has active rides", driverId);

        List<Ride> activeRides = rideRepository.findActiveRidesByDriverId(driverId);

        boolean hasActive = !activeRides.isEmpty();
        log.info("Driver ID: {} has active rides: {}", driverId, hasActive);

        return hasActive;
    }

    @Override
    public DriverRideHistoryDTO getCurrentActiveRide(Long driverId) {
        log.info("Fetching current active ride for driver ID: {}", driverId);

        List<Ride> activeRides = rideRepository.findActiveRidesByDriverId(driverId);

        if (activeRides.isEmpty()) {
            log.info("No active ride found for driver ID: {}", driverId);
            return null;
        }

        Ride activeRide = activeRides.get(0);
        log.info("Found active ride ID: {} for driver ID: {}", activeRide.getId(), driverId);

        return mapToDriverRideHistoryDTO(activeRide);
    }


    private DriverRideHistoryDTO mapToDriverRideHistoryDTO(Ride ride) {
        List<DriverRideHistoryDTO.PassengerDTO> passengers = new ArrayList<>();

        if (ride.getPassenger() != null) {
            passengers.add(mapToPassengerDTO(ride.getPassenger()));
        }

        // Add linked passengers
        if (ride.getLinkedPassengers() != null) {
            ride.getLinkedPassengers().stream()
                    .map(this::mapToPassengerDTO)
                    .forEach(passengers::add);
        }

        return DriverRideHistoryDTO.builder()
                .rideId(ride.getId())
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .startLocation(ride.getStartLocation())
                .startAddress(ride.getStartAddress())
                .endLocation(ride.getEndLocation())
                .endAddress(ride.getEndAddress())
                .totalPrice(ride.getTotalCost())
                .cancelled(ride.getStatus() == RideStatus.CANCELLED)
                .cancelledBy(ride.getCancelledBy() != null ? ride.getCancelledBy().name() : null)
                .panicActivated(ride.getPanicActivated() != null ? ride.getPanicActivated() : false)
                .passengers(passengers)
                .build();
    }

    private DriverRideHistoryDTO.PassengerDTO mapToPassengerDTO(Passenger passenger) {
        return DriverRideHistoryDTO.PassengerDTO.builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .email(passenger.getEmail())
                .phoneNumber(passenger.getPhoneNumber())
                .profileImage(passenger.getProfilePicture())
                .build();
    }

    private RideDetailsDTO mapToRideDetailsDTO(Ride ride) {
        RouteDTO route = RouteDTO.builder()
                .geometry(ride.getWayPoints() != null
                        ? ride.getWayPoints().stream()
                        .map(WayPoint::getLocation)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .distanceMeters(ride.getTotalDistance() != null ? ride.getTotalDistance() * 1000 : 0)
                .durationSeconds(ride.getEstimatedDuration() != null ? ride.getEstimatedDuration() * 60 : 0)
                .build();

        return RideDetailsDTO.builder()
                .rideId(ride.getId())
                .route(route)
                .cancelled(ride.getStatus() == RideStatus.CANCELLED)
                .cancelledBy(ride.getCancelledBy() != null ? ride.getCancelledBy().name() : null)
                .price(ride.getTotalCost())
                .panicTriggered(ride.getPanicActivated() != null ? ride.getPanicActivated() : false)
                .build();
    }

    @Transactional
    @Override
    public Ride createRide(RideCreateRequestDTO rideCreateRequestDTO) {
        Ride ride = new Ride();
        List<WayPointDTO> wayPoints = new ArrayList<>(rideCreateRequestDTO.getCoordinates());
        ride.setStartLocation(wayPoints.get(0).getLocation());
        ride.setEndLocation(wayPoints.get(wayPoints.size()-1).getLocation());

        wayPoints.remove(wayPoints.size()-1);
        wayPoints.remove(0);

        if(!wayPoints.isEmpty()) {
            List<WayPoint> wayPoints1 = new ArrayList<>();
            for(WayPointDTO wayPoint : wayPoints) {
                WayPoint wayPoint1 = new WayPoint();
                wayPoint1.setLocation(wayPoint.getLocation());
                wayPoint1.setAddress(wayPoint.getAddress());
                wayPoint1.setOrderIndex(wayPoint.getOrderIndex());
                wayPoints1.add(wayPoint1);
            }
            ride.setWayPoints(wayPoints1);
        }

        ride.setPassengerNumber(rideCreateRequestDTO.getPassengerNumber());
        if(rideCreateRequestDTO.isRideNow()){
            ride.setStartTime(LocalDateTime.now());
        }else{
            ride.setStartTime(rideCreateRequestDTO.getRideTime());
        }
        List<Passenger>  passengers = new ArrayList<>();
        for(String email:rideCreateRequestDTO.getPassengerEmails()){
            Optional<User> user=userRepository.findByEmail(email);
            if(user.isPresent()) {
                Passenger passenger = (Passenger) user.get();
                passengers.add(passenger);
            }
        }
        ride.setLinkedPassengers(passengers);
        ride.setVehicleType(rideCreateRequestDTO.getVehicleType());
        ride.setBabySeat(rideCreateRequestDTO.isBabySeat());
        ride.setPetFriendly(rideCreateRequestDTO.isPetFriendly());
        ride.setStartAddress(rideCreateRequestDTO.getStartAddress());
        ride.setEndAddress(rideCreateRequestDTO.getEndAddress());
        ride.setTotalDistance(rideCreateRequestDTO.getTotalDistance());
        ride.setEstimatedDuration(rideCreateRequestDTO.getEstimatedDuration());
        ride.setTotalCost(priceCalculationService.calculatePrice(
                rideCreateRequestDTO.getTotalDistance(),
                rideCreateRequestDTO.getVehicleType()
        ));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Passenger passenger = (Passenger) authentication.getPrincipal();
        ride.setPassenger(passenger);
        ride.setStatus(RideStatus.ACCEPTED);
        if(rideCreateRequestDTO.isRideNow()) {
            Driver driver = driverService.findAvailableDriver(ride);
            if (driver == null) {
                return null;
            }
            driver.setAvailable(false);
            userRepository.save(driver);
            ride.setDriver(driver);
        }else{
            if(!driverService.checkAlreadyBookedDateTime(ride)){
                return null;
            }
        }
        rideRepository.save(ride);
        return ride;
    }

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.getRideById(rideId);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerRideDetailsDTO getPassengerRideDetails(Long passengerId, Long rideId) {

        Ride ride = rideRepository
                .findPassengerRideWithDetails(passengerId, rideId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Ride not found or access denied")
                );

        List<RideLocation> locations =
                rideLocationRepository.findByRideAndRecordedAtAfterOrderByRecordedAtAsc(
                        ride,
                        ride.getStartTime()
                );

        List<InconsistencyReport> reports =
                inconsistencyReportRepository.findByRideId(rideId);

        return PassengerRideDetailsDTO.builder()
                .id(ride.getId())
                .startAddress(ride.getStartAddress())
                .endAddress(ride.getEndAddress())
                .startLocation(ride.getStartLocation())
                .endLocation(ride.getEndLocation())
                .wayPoints(
                        ride.getWayPoints().stream()
                                .map(WayPoint::getLocation)
                                .toList()
                )
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .totalDistance(ride.getTotalDistance())
                .estimatedDuration(ride.getEstimatedDuration())
                .totalCost(ride.getTotalCost())
                .status(ride.getStatus())
                .vehicleType(ride.getVehicleType())
                .petFriendly(ride.isPetFriendly())
                .babySeat(ride.isBabySeat())
                .driver(
                        DriverInfoDTO.builder()
                                .id(ride.getDriver().getId())
                                .firstName(ride.getDriver().getFirstName())
                                .lastName(ride.getDriver().getLastName())
                                .phoneNumber(ride.getDriver().getPhoneNumber())
                                .profileImage(ride.getDriver().getProfilePicture())
                                .build()
                )
                // real tracked route as GeoPoints
                .realRoute(
                        locations.stream()
                                .map(loc -> new GeoPoint(
                                        loc.getLatitude(),
                                        loc.getLongitude()
                                ))
                                .toList()
                )
                .hasInconsistencies(!reports.isEmpty())
                .inconsistencies(
                        reports.stream()
                                .map(r -> InconsistencyReportResponseDTO.builder()
                                        .reportId(r.getId())
                                        .message(r.getDescription())
                                        .reportedAt(r.getReportedAt())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }




    @Override
    public Page<PassengerRideHistoryDto> getPassengerRideHistory(
            Long passengerId,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        //todo remove later
        log.info(
                "Fetching completed ride history for passenger {} from {} to {}, page {}, size {}",
                passengerId, from, to, page, size
        );

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return rideRepository.findPassengerRideHistory(
                passengerId,
                from,
                to,
                pageable
        );
    }


}