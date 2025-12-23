package com.ognjen.fleetforge.model;

public enum UserRole {
    UNREGISTERED("Unregistered User"),
    USER("User"),
    DRIVER("Driver"),
    ADMIN("Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}