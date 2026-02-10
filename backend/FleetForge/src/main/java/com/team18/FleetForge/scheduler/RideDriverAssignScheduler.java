package com.team18.FleetForge.scheduler;

import com.team18.FleetForge.model.enums.NotificationType;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.repository.rides.RideRepository;
import com.team18.FleetForge.service.NotificationService;
import com.team18.FleetForge.service.users.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideDriverAssignScheduler {
    private final DriverService driverService;
    private final RideRepository rideRepo;
    private final NotificationService notificationService;
    @Scheduled(fixedRate = 60000)
    public void checkAndAssignRides() {
        System.out.println("Scheduler pokrenut: Proveravam zakazane vožnje...");
        driverService.setDriversScheduledRides();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void notifyDrivers(){
        ArrayList<Ride> ridesIn15OrLess=(ArrayList<Ride>) rideRepo.findAcceptedRidesForReminders(LocalDateTime.now().plusMinutes(16));
        for(Ride ride :ridesIn15OrLess){
            long minutesUntil = ChronoUnit.MINUTES.between(LocalDateTime.now(), ride.getStartTime());
             if(!ride.isNotificationSent5Min()&& minutesUntil<=5){
                notificationService.sendNotificationToUser(ride.getDriver(), NotificationType.RIDE_IN_5_MIN,"There is a scheduled ride in " +minutesUntil +" minutes.",ride);
                ride.setNotificationSent5Min(true);
                ride.setNotificationSent10Min(true);
                ride.setNotificationSent15Min(true);
                rideRepo.save(ride);
                 log.info("Sent 5-min reminder for ride {} ({} min left)", ride.getId(), minutesUntil);
            }else if(!ride.isNotificationSent10Min()&& minutesUntil<=10){
                notificationService.sendNotificationToUser(ride.getDriver(), NotificationType.RIDE_IN_10_MIN,"There is a scheduled ride in " +minutesUntil +" minutes.",ride);
                ride.setNotificationSent10Min(true);
                ride.setNotificationSent15Min(true);
                rideRepo.save(ride);
                 log.info("Sent 10-min reminder for ride {} ({} min left)", ride.getId(), minutesUntil);
            }else if(!ride.isNotificationSent15Min()&& minutesUntil<=15){
                notificationService.sendNotificationToUser(ride.getDriver(), NotificationType.RIDE_IN_15_MIN,"There is a scheduled ride in " +minutesUntil +" minutes.",ride);
                ride.setNotificationSent15Min(true);
                rideRepo.save(ride);
                 log.info("Sent 15-min reminder for ride {} ({} min left)", ride.getId(), minutesUntil);
            }
        }
    }
}
