package com.team18.FleetForge.model.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_PASSENGER,
    ROLE_DRIVER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
