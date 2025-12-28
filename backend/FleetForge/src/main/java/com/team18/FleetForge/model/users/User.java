package com.team18.FleetForge.model.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {

    private Long id;

    private String email;
    private String password;

    private String firstName;
    private String lastName;

    private String address;
    private String phoneNumber;

    private String profilePicture;

    private boolean isBlocked;
    private String blockReason;
}

