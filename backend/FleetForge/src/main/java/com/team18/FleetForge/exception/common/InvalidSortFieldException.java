package com.team18.FleetForge.exception.common;

public class InvalidSortFieldException extends RuntimeException {

    public InvalidSortFieldException(String field) {
        super("Sorting by '" + field + "' is not allowed");
    }
}
