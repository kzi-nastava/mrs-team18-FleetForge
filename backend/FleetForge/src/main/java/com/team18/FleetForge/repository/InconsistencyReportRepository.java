package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.ride.InconsistencyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InconsistencyReportRepository extends JpaRepository<InconsistencyReport, Long> {

    // Find all inconsistency reports for a specific ride
    List<InconsistencyReport> findByRideId(Long rideId);

    // Find all inconsistency reports for a specific passenger
    List<InconsistencyReport> findByReporterId(Long reporterId);
}