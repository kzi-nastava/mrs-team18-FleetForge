package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find all rides for specific driver, optionally filtered by start date.
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND (:startDate IS NULL OR r.startTime >= :startDateTime) " +
            "ORDER BY r.startTime DESC")
    List<Ride> findDriverRideHistory(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDate startDate,
            @Param("startDateTime") LocalDateTime startDateTime
    );

    // Find all rides for a specific driver.
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId ORDER BY r.startTime DESC")
    List<Ride> findAllByDriverId(@Param("driverId") Long driverId);

    // Find rides for a driver within a specific date range.
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.startTime >= :startDateTime " +
            "AND r.startTime < :endDateTime " +
            "ORDER BY r.startTime DESC")
    List<Ride> findDriverRidesByDateRange(
            @Param("driverId") Long driverId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // Find active rides (PENDING, ACCEPTED, IN_PROGRESS) for a specific driver.
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS') " +
            "ORDER BY r.startTime ASC")
    List<Ride> findActiveRidesByDriverId(@Param("driverId") Long driverId);


    Optional<Ride> findByIdAndStatus(Long id, RideStatus status);

    // Find all rides by status
    List<Ride> findByStatus(RideStatus status);

    // Find active ride for a passenger (including linked passengers)
    @Query("SELECT r FROM Ride r WHERE r.status = :status " +
            "AND (r.passenger.id = :passengerId OR :passengerId IN " +
            "(SELECT lp.id FROM r.linkedPassengers lp))")
    Optional<Ride> findActiveRideByPassengerId(
            @Param("passengerId") Long passengerId,
            @Param("status") RideStatus status
    );

    @NonNull
    List<Ride> findAllByStatus(RideStatus status);
    List<Ride> findAllByDriver(Driver driver);
    List<Ride> findAllByDriverAndStatus(Driver driver, RideStatus status);
    Ride getRideById(Long rideId);
}