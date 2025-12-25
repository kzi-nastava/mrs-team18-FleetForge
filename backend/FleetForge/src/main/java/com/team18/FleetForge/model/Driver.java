package com.team18.FleetForge.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int phone;
    private String address;
    private Vehicle vehicle;
}
