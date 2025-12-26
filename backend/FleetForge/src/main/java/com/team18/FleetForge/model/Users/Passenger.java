package com.team18.FleetForge.model.Users;

import com.team18.FleetForge.model.Route;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends User {
    private List<Route> favoriteRoutes;
}
