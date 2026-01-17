package com.team18.FleetForge.model.ride;

import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.users.Passenger;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "inconsistency_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InconsistencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Passenger reporter;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "report_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "report_longitude"))
    })
    private GeoPoint reportLocation;
}