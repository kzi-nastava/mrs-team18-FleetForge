package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.ride.lifecycle.FinishRideRequestDTO;
import com.team18.FleetForge.dto.ride.lifecycle.FinishRideResponseDTO;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.RideRepository;
import com.team18.FleetForge.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideFinishService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final EmailService emailService;

    @Transactional
    public FinishRideResponseDTO finishRide(FinishRideRequestDTO request) {
        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + request.getRideId()));

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot finish ride. Ride is not in progress. Current status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());
        rideRepository.save(ride);

        Driver driver = ride.getDriver();

        // TODO: Check for scheduled rides when that feature is implemented
        Long nextScheduledRideId = null;
        driver.setAvailable(true);
        driverRepository.save(driver);

        sendRideCompletionEmails(ride);

        return FinishRideResponseDTO.builder()
                .rideId(ride.getId())
                .status(RideStatus.COMPLETED)
                .endTime(ride.getEndTime())
                .totalCost(ride.getTotalCost())
                .message("Ride completed successfully")
                .driverAvailable(driver.isAvailable())
                .nextScheduledRideId(nextScheduledRideId)
                .build();
    }

    private void sendRideCompletionEmails(Ride ride) {
        List<Passenger> allPassengers = new ArrayList<>();
        allPassengers.add(ride.getPassenger());
        if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
            allPassengers.addAll(ride.getLinkedPassengers());
        }

        for (Passenger passenger : allPassengers) {
            String subject = "Ride Completed - FleetForge";
            String body = buildCompletionEmailBody(ride, passenger);

            try {
                emailService.sendEmail(passenger.getEmail(), subject, body);
            } catch (Exception e) {
                System.err.println("Failed to send completion email to: " + passenger.getEmail());
            }
        }
    }

    private String buildCompletionEmailBody(Ride ride, Passenger passenger) {
        return String.format(
                "Dear %s %s,\n\n" +
                        "Your ride has been completed successfully!\n\n" +
                        "Ride Details:\n" +
                        "- From: %s\n" +
                        "- To: %s\n" +
                        "- Start Time: %s\n" +
                        "- End Time: %s\n" +
                        "- Total Cost: %.2f RSD\n\n" +
                        "Thank you for using FleetForge!\n\n" +
                        "You can now rate your ride and driver through the application.\n\n" +
                        "Best regards,\n" +
                        "FleetForge Team",
                passenger.getFirstName(),
                passenger.getLastName(),
                ride.getStartAddress(),
                ride.getEndAddress(),
                ride.getStartTime(),
                ride.getEndTime(),
                ride.getTotalCost()
        );
    }
}