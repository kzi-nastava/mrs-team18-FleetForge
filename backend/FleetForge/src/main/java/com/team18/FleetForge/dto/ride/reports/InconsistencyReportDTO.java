package com.team18.FleetForge.dto.ride.reports;

import com.team18.FleetForge.model.GeoPoint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InconsistencyReportDTO {

    @NotBlank(message = "Comment is required")
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    private GeoPoint currentLocation;
}