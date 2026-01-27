package com.team18.FleetForge.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsDTO {
    private Double totalEarnings;
    private Double hoursWorked;
    private Integer ridesCompleted;
}
