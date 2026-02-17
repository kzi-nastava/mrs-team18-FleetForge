package com.ognjen.fleetforge.dtos.reports;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UserDataReportResponseDTO {
    private Map<String, List<ReportsRideDTO>> dataByDay;

    public Map<String, List<ReportsRideDTO>> getDataByDay() {
        return dataByDay;
    }

    public void setDataByDay(Map<String, List<ReportsRideDTO>> dataByDay) {
        this.dataByDay = dataByDay;
    }
}
