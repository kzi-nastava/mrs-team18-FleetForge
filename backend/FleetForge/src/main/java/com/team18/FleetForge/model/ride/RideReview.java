package com.team18.FleetForge.model.ride;

import com.team18.FleetForge.model.users.Passenger;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @Column(name = "vehicle_rating", nullable = false)
    private Integer vehicleRating;

    @Column(name = "driver_rating", nullable = false)
    private Integer driverRating;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;
}