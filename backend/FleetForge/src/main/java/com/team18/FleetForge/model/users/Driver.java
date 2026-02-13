package com.team18.FleetForge.model.users;


import com.team18.FleetForge.model.ride.GeoPoint;
import com.team18.FleetForge.model.vehicles.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends User {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    private Vehicle vehicle;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "is_active")
    private boolean isActive;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "current_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "current_longitude"))
    })
    private GeoPoint currentLocation;
}