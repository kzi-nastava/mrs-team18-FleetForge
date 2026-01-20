package com.team18.FleetForge.scheduler;

import com.team18.FleetForge.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideDriverAssignScheduler {
    private final DriverService driverService;
    @Scheduled(fixedRate = 60000)
    public void checkAndAssignRides() {
        System.out.println("Scheduler pokrenut: Proveravam zakazane vožnje...");
        driverService.setDriversScheduledRides();
    }
}
