package com.team18.FleetForge.repository.rides;

import com.team18.FleetForge.dto.ride.view.AdminRideHistoryDTO;
import com.team18.FleetForge.dto.ride.view.PassengerRideHistoryDto;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
        SELECT r FROM Ride r
        LEFT JOIN FETCH r.driver
        LEFT JOIN FETCH r.passenger
        LEFT JOIN FETCH r.linkedPassengers
        LEFT JOIN FETCH r.review
        WHERE r.id = :rideId
    """)
    Optional<Ride> findRideWithDetails(@Param("rideId") Long rideId);


    // Find active rides (ACCEPTED, IN_PROGRESS) for a specific driver.
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.status IN ('ACCEPTED', 'IN_PROGRESS') " +
            "ORDER BY r.startTime ASC")
    List<Ride> findActiveRidesByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.status IN ('COMPLETED', 'CANCELLED')"+
            "ORDER BY r.startTime ASC")
    List<Ride> findCompletedRidesByDriverId(@Param("driverId") Long driverId);


    // Find active ride for a passenger (including linked passengers)
    @Query("SELECT r FROM Ride r WHERE " +
            "(r.status IN ('ACCEPTED', 'IN_PROGRESS') OR " +
            "(r.status = 'COMPLETED' AND r.endTime >= :fiveMinutesAgo AND r.endTime <= :now)) " +
            "AND (r.passenger.id = :passengerId OR :passengerId IN " +
            "(SELECT lp.id FROM r.linkedPassengers lp))")
    List<Ride> findActiveRidesByPassengerId(
            @Param("passengerId") Long passengerId,
            @Param("fiveMinutesAgo") LocalDateTime fiveMinutesAgo,
            @Param("now") LocalDateTime now
    );


    List<Ride> findAllByDriverAndStatusAndEndTimeBetween(
            Driver driver,
            RideStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query("""
    select r from Ride r
    left join fetch r.driver d
    left join fetch r.wayPoints wp
    where r.id = :rideId
      and (
           r.passenger.id = :passengerId
           or exists (
               select lp.id from r.linkedPassengers lp
               where lp.id = :passengerId
           )
      )
""")
    Optional<Ride> findPassengerRideWithDetails(
            @Param("passengerId") Long passengerId,
            @Param("rideId") Long rideId
    );


    @Query("""
    SELECT new com.team18.FleetForge.dto.ride.view.PassengerRideHistoryDto(
         r.id,
         r.startTime,
         r.endTime,
         r.startAddress,
         r.endAddress,
         rr.vehicleRating,
         rr.driverRating,
         r.status
     )
    FROM Ride r
    LEFT JOIN r.review rr
    WHERE
        r.status IN ('COMPLETED', 'CANCELLED')
        AND (r.passenger.id = :passengerId
            OR :passengerId IN (
                SELECT lp.id FROM r.linkedPassengers lp
            )
        )
        AND (:from IS NULL OR r.startTime >= :from)
        AND (:to IS NULL OR r.startTime <= :to)
    """)
    Page<PassengerRideHistoryDto> findPassengerRideHistory(
            @Param("passengerId") Long passengerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Query("SELECT r FROM Ride r " +
        "WHERE r.passenger.id = :passengerId " +
        "AND r.status = 'ACCEPTED' " +
        "AND r.startTime >= :now " +
        "ORDER BY r.startTime ASC")
    Page<Ride> findAcceptedNotStartedRides(
            @Param("passengerId") Long passengerId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
        SELECT new com.team18.FleetForge.dto.ride.view.AdminRideHistoryDTO(
            r.id,
            r.startTime,
            r.endTime,
            r.startAddress,
            r.endAddress,
            r.panicActivated,
            r.status
        )
        FROM Ride r
        WHERE
            r.status IN ('COMPLETED', 'CANCELLED')
            AND (
                (:userId IS NOT NULL AND (
                    r.passenger.id = :userId
                    OR r.driver.id = :userId
                    OR :userId IN (
                        SELECT lp.id FROM r.linkedPassengers lp
                    )
                ))
                OR
                (:email IS NOT NULL AND (
                    r.passenger.email = :email
                    OR r.driver.email = :email
                    OR :email IN (
                        SELECT lp.email FROM r.linkedPassengers lp
                    )
                ))
            )
            AND (:from IS NULL OR r.startTime >= :from)
            AND (:to IS NULL OR r.startTime <= :to)
    """)
    Page<AdminRideHistoryDTO> findAdminRideHistory(
            @Param("userId") Long userId,
            @Param("email") String email,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @NonNull
    List<Ride> findAllByStatus(RideStatus status);
    List<Ride> findAllByDriver(Driver driver);
    List<Ride> findAllByDriverAndStatus(Driver driver, RideStatus status);
    Ride getRideById(Long rideId);

    @Query("SELECT DISTINCT r FROM Ride r " +
            "LEFT JOIN FETCH r.driver " +
            "LEFT JOIN FETCH r.passenger " +
            "LEFT JOIN FETCH r.linkedPassengers where " +
            "r.status = 'ACCEPTED' AND " +
            "r.startTime > CURRENT_TIMESTAMP AND " +
            "r.startTime <= :endWindow AND " +
            "(r.notificationSent15Min = false OR " +
            " r.notificationSent10Min = false OR " +
            " r.notificationSent5Min = false)")
    List<Ride> findAcceptedRidesForReminders(@Param("endWindow") LocalDateTime endWindow);

    @Query("select r from Ride r " +
            "left join fetch r.driver " +
            "left join fetch r.passenger " +
            "left join fetch  r.linkedPassengers where " +
            "r.endTime is not null and r.startTime >= :fromDate and r.endTime<= :toDate and " +
            "(r.passenger.id = :userId or r.driver.id = :userId) ")
    List<Ride> findRidesForPassengerForGivenDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("userId") Long userId);

    @Query("select r from Ride r " +
            "left join fetch r.driver " +
            "left join fetch r.passenger " +
            "left join fetch  r.linkedPassengers where " +
            "r.endTime is not null and r.startTime >= :fromDate and r.endTime<= :toDate ")
    List<Ride> findRidesForGivenDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query("SELECT r FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.status = com.team18.FleetForge.model.enums.RideStatus.IN_PROGRESS " +
            "AND r.panicActivated = true " +
            "AND r.isPanicHandled = false")
    Optional<Ride> findActivePanicRideByDriver(@Param("driverId") Long driverId);
}