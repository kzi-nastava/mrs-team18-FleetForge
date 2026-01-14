package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.users.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
