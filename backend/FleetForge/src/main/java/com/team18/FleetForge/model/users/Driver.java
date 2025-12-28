package com.team18.FleetForge.model.users;


import com.team18.FleetForge.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends User {
    private Vehicle vehicle;
}
