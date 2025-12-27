package com.team18.FleetForge.dto.ride.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideReviewRequestDTO {

    @NotNull(message = "Vehicle rating is required")
    @Min(value = 1, message = "Vehicle rating must be between 1 and 5")
    @Max(value = 5, message = "Vehicle rating must be between 1 and 5")
    private Integer vehicleRating;

    @NotNull(message = "Driver rating is required")
    @Min(value = 1, message = "Driver rating must be between 1 and 5")
    @Max(value = 5, message = "Driver rating must be between 1 and 5")
    private Integer driverRating;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment; // Optional
}