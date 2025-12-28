package com.team18.FleetForge.model.users;

import com.team18.FleetForge.model.Route;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Passenger extends User {
    private List<Route> favoriteRoutes;
}
