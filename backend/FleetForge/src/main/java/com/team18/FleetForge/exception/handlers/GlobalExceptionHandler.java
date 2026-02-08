package com.team18.FleetForge.exception.handlers;

import com.team18.FleetForge.exception.common.InvalidSortFieldException;
import com.team18.FleetForge.exception.common.UserIdentifierRequiredException;
import com.team18.FleetForge.exception.ride.RideNotFoundException;
import com.team18.FleetForge.exception.user.InvalidUserRoleException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.query.sqm.PathElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<String> handleInvalidSortField(InvalidSortFieldException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserIdentifierRequiredException.class)
    public ResponseEntity<String> handleUserIdentifierRequired(
            UserIdentifierRequiredException ex) {

        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUserRoleException.class)
    public ResponseEntity<String> handleInvalidUserRole(
            InvalidUserRoleException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<String> handleRideNotFound(RideNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(org.hibernate.query.sqm.PathElementException.class)
    public ResponseEntity<String> handleInvalidPath(PathElementException ex) {
        return ResponseEntity
                .badRequest()
                .body("Invalid query parameter");
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

}
