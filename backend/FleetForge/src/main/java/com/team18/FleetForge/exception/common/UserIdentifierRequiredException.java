package com.team18.FleetForge.exception.common;

public class UserIdentifierRequiredException extends RuntimeException {
    public UserIdentifierRequiredException() {
        super("Either userId or email must be provided");
    }
}