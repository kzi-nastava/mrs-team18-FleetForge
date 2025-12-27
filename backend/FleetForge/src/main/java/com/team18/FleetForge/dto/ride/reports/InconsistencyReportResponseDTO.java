package com.team18.FleetForge.dto.ride.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InconsistencyReportResponseDTO {
    private Long reportId;
    private String message;
    private LocalDateTime reportedAt;
}