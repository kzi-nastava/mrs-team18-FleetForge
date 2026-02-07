package com.team18.FleetForge.model.ride;

import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.enums.RideActor;
import com.team18.FleetForge.model.enums.RideStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "start_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "start_longitude"))
    })
    private GeoPoint startLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "end_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "end_longitude"))
    })
    private GeoPoint endLocation;

    @Column(name = "start_address")
    private String startAddress;

    @Column(name = "end_address")
    private String endAddress;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "ride_id")
    @OrderBy("orderIndex ASC")
    private List<WayPoint> wayPoints = new ArrayList<>();

    @Column(name = "total_distance")
    private Double totalDistance; // in kilometers

    @Column(name = "estimated_duration")
    private Double estimatedDuration; // in minutes

    @Column(name = "total_cost")
    private Double totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ride_linked_passengers",
            joinColumns = @JoinColumn(name = "ride_id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id")
    )
    private List<Passenger> linkedPassengers = new ArrayList<>();

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RideStatus status;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by")
    private RideActor cancelledBy;

    @Column(name = "panic_activated")
    private Boolean panicActivated = false;

    @Column(name = "panic_activated_at")
    private LocalDateTime panicActivatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "panic_initiator")
    private RideActor panicInitiator;

    @Column(name = "passenger_number")
    private int passengerNumber;

    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @Column(name = "pet_friendly")
    private boolean isPetFriendly;

    @Column(name = "baby_seat")
    private boolean isBabySeat;
}