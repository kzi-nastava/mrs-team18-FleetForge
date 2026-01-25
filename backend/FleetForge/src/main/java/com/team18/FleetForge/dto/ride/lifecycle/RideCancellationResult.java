package com.team18.FleetForge.dto.ride.lifecycle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class RideCancellationResult {
    private boolean success;
    private String message;
    private HttpStatus httpStatus;
}

