package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT p FROM Passenger p WHERE p.isActivated = true")
    List<Passenger> findAllActivePassengers();

    @Query("SELECT p FROM Passenger p WHERE p.isBlocked = true")
    List<Passenger> findAllBlockedPassengers();

    Passenger findByEmail(String email);
}
