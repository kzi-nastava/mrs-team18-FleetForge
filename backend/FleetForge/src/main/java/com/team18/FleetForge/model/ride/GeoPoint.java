package com.team18.FleetForge.model.ride;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Embeddable;

@Embeddable  // Add this annotation
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoPoint {
    private double latitude;
    private double longitude;
}