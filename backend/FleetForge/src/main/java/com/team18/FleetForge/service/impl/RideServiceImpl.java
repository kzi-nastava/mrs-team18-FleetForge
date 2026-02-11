package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.dto.RouteDTO;
import com.team18.FleetForge.dto.UserSummaryDTO;
import com.team18.FleetForge.dto.driver.DriverInfoDTO;
import com.team18.FleetForge.dto.driver.DriverRideHistoryDTO;
import com.team18.FleetForge.dto.ride.lifecycle.RideCreateRequestDTO;
import com.team18.FleetForge.dto.ride.reports.InconsistencyReportResponseDTO;
import com.team18.FleetForge.dto.ride.review.RideRatingDTO;
import com.team18.FleetForge.dto.ride.routes.WayPointDTO;
import com.team18.FleetForge.dto.ride.view.*;
import com.team18.FleetForge.exception.common.InvalidSortFieldException;
import com.team18.FleetForge.exception.common.UserIdentifierRequiredException;
import com.team18.FleetForge.exception.ride.RideNotFoundException;
import com.team18.FleetForge.exception.user.InvalidUserRoleException;
import com.team18.FleetForge.model.enums.NotificationType;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.ride.*;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.rides.InconsistencyReportRepository;
import com.team18.FleetForge.repository.rides.RideLocationRepository;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import com.team18.FleetForge.service.EmailService;
import com.team18.FleetForge.service.NotificationService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final EmailService emailService;
    private final NotificationService notificationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "startTime",
            "endTime",
            "price",
            "status",
            "startAddress",
            "endAddress"
    );


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
                .orElseThrow(() -> new RideNotFoundException("Ride not found with ID: " + rideId));

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
                notificationService.sendNotificationToUser(passenger,NotificationType.NO_AVAILABLE_DRIVER,"There are no available drivers. From: "+ride.getStartAddress()+ " to: "+ride.getEndAddress(),null);
                return null;
            }
            driver.setAvailable(false);
            userRepository.save(driver);
            ride.setDriver(driver);
            notificationService.sendNotificationToUser(driver,NotificationType.RIDE_CREATED,"New ride! From: "+ride.getStartAddress()+" to: "+ride.getEndAddress(),ride);
        }else{
            if(!driverService.checkAlreadyBookedDateTime(ride)){
                notificationService.sendNotificationToUser(passenger,NotificationType.NO_AVAILABLE_DRIVER,"There are no available drivers. From: "+ride.getStartAddress()+ " to: "+ride.getEndAddress(),null);
                return null;
            }
        }
         rideRepository.save(ride);

        if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
            for (Passenger linkedPassenger : ride.getLinkedPassengers()) {
                emailService.sendRideNotificationEmail(linkedPassenger.getEmail(), ride);
                System.out.println("Sending email to: " +  linkedPassenger.getEmail());
            }
        }

        notificationService.sendNotificationToLinkedPassengers(
                ride,
                NotificationType.RIDE_CREATED,
                "You have been added to a ride"
        );


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
                        new RideNotFoundException("Ride not found or access denied")
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
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new InvalidSortFieldException(sortBy);
        }

        String resolvedSortBy;
        if (sortBy.startsWith("review.")) {
            resolvedSortBy = sortBy.replace("review.", "rr.");
        } else if (sortBy.equals("vehicleRating") || sortBy.equals("driverRating")) {
            resolvedSortBy = "rr." + sortBy;
        } else {
            resolvedSortBy = "r." + sortBy;
        }

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(new Sort.Order(sortDirection, resolvedSortBy).nullsLast());
        Pageable pageable = PageRequest.of(page, size, sort);

        return rideRepository.findPassengerRideHistory(
                passengerId,
                from,
                to,
                pageable
        );
    }

    @Override
    public Page<ScheduledRideDTO> getScheduledRidesForPassenger(
            Long passengerId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        LocalDateTime now = LocalDateTime.now();

        return rideRepository.findAcceptedNotStartedRides(passengerId, now, pageable)
                .map(ride -> new ScheduledRideDTO(
                        ride.getId(),
                        ride.getStartAddress(),
                        ride.getEndAddress(),
                        ride.getStartTime(),
                        ride.getTotalCost(),
                        ride.getStatus().name()
                ));
    }


    @Override
    public Page<AdminRideHistoryDTO> getAdminRideHistory(
            Long userId,
            String email,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        if (userId == null && email == null) {
            throw new UserIdentifierRequiredException();
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new InvalidSortFieldException(sortBy);
        }

        validateNonAdminUser(userId, email);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Sort sort = Sort.by(new Sort.Order(sortDirection, sortBy).nullsLast());
        Pageable pageable = PageRequest.of(page, size, sort);

        return rideRepository.findAdminRideHistory(
                userId,
                email,
                from,
                to,
                pageable
        );
    }

    private void validateNonAdminUser(Long userId, String email) {

        User user;

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "User with id " + userId + " does not exist"
                            )
                    );
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "User with email " + email + " does not exist"
                            )
                    );
        }

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new InvalidUserRoleException(
                    "Admins cannot be queried for ride history"
            );
        }
    }

    @Transactional(readOnly = true)
    public AdminRideDetailsDTO getAdminRideDetails(Long rideId) {

        Ride ride = rideRepository.findRideWithDetails(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        List<RideLocation> locations = rideLocationRepository
                .findByRideAndRecordedAtAfterOrderByRecordedAtAsc(ride, ride.getStartTime());

        List<InconsistencyReport> reports = inconsistencyReportRepository.findByRideId(rideId);

        UserSummaryDTO mainPassenger = UserSummaryDTO.builder()
                .id(ride.getPassenger().getId())
                .firstName(ride.getPassenger().getFirstName())
                .lastName(ride.getPassenger().getLastName())
                .build();

        List<UserSummaryDTO> linkedPassengers = ride.getLinkedPassengers().stream()
                .map(p -> UserSummaryDTO.builder()
                        .id(p.getId())
                        .firstName(p.getFirstName())
                        .lastName(p.getLastName())
                        .build()
                ).toList();

        RideRatingDTO ratings = null;
        if (ride.getReview() != null) {
            ratings = RideRatingDTO.builder()
                    .driverRating(ride.getReview().getDriverRating())
                    .vehicleRating(ride.getReview().getVehicleRating())
                    .build();
        }

        return AdminRideDetailsDTO.builder()
                .id(ride.getId())
                .startAddress(ride.getStartAddress())
                .endAddress(ride.getEndAddress())
                .startLocation(ride.getStartLocation())
                .endLocation(ride.getEndLocation())
                .wayPoints(ride.getWayPoints().stream()
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
                .driver(DriverInfoDTO.builder()
                        .id(ride.getDriver().getId())
                        .firstName(ride.getDriver().getFirstName())
                        .lastName(ride.getDriver().getLastName())
                        .phoneNumber(ride.getDriver().getPhoneNumber())
                        .profileImage(ride.getDriver().getProfilePicture())
                        .build()
                )
                .mainPassenger(mainPassenger)
                .linkedPassengers(linkedPassengers)
                .cancelledBy(ride.getCancelledBy() != null ? ride.getCancelledBy().name() : null)
                .cancellationReason(ride.getCancellationReason())
                .ratings(ratings)
                .realRoute(locations.stream()
                        .map(loc -> new GeoPoint(loc.getLatitude(), loc.getLongitude()))
                        .toList()
                )
                .hasInconsistencies(!reports.isEmpty())
                .inconsistencies(reports.stream()
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

}