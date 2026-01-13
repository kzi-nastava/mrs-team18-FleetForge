package com.team18.FleetForge.model.users;

import com.team18.FleetForge.model.Route;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "passengers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Passenger extends User {
    //todo remove later
    @Transient
    private List<Route> favoriteRoutes;
}
