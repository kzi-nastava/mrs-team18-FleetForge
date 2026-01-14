package com.team18.FleetForge.model.ride;

import com.team18.FleetForge.model.GeoPoint;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waypoints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WayPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "longitude"))
    })
    private GeoPoint location;

    @Column(name = "address")
    private String address;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "ride_id", insertable = false, updatable = false)
    private Long rideId;
}