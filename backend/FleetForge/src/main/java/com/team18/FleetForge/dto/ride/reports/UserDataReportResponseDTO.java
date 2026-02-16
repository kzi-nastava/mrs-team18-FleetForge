package com.team18.FleetForge.dto.ride.reports;

import com.team18.FleetForge.model.ride.Ride;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataReportResponseDTO {
    private Map<LocalDate, List<ReportsRideDTO>> dataByDay;
}
