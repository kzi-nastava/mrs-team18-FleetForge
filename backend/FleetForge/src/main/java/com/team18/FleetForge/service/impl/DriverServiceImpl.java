package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.DriverSession;
import com.team18.FleetForge.model.enums.RideStatus;
import com.team18.FleetForge.model.enums.VehicleType;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.repository.DriverRepository;
import com.team18.FleetForge.repository.DriverSessionRepo;
import com.team18.FleetForge.repository.RideRepository;
import com.team18.FleetForge.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final DriverSessionRepo driverSessionRepo;
    @Override
    public Driver findAvailableDriver(Ride ride) {
        List<Driver> activeDrivers = driverRepository.findByIsActiveTrue();
        if(activeDrivers.isEmpty())
            return null;
        List<Driver> availableDrivers = driverRepository.findByIsAvailableTrue();
        if(availableDrivers.isEmpty()){
            List<Driver> canDriveNext=new ArrayList<>();
            for(Driver driver:activeDrivers){
                if(canDriverRideNextRide(driver,ride) &&
                        checkDriverActivity(driverSessionRepo.findByDriver(driver),false)) {
                    canDriveNext.add(driver);
                }
            }
            List<Driver> filteredByVehicleType=new ArrayList<>();
            for(Driver driver:canDriveNext){
                if(driver.getVehicle().getType()==ride.getVehicleType()){
                    filteredByVehicleType.add(driver);
                }
            }
            if(filteredByVehicleType.isEmpty()) {
                return null;
            }
            Driver driver = scoring(filteredByVehicleType, ride);
            return driver;
        }else{
            List<Driver> validAvailableDrivers = new ArrayList<>();
            for(Driver driver : availableDrivers) {
                if(checkDriverActivity(driverSessionRepo.findByDriver(driver),false)) {
                    validAvailableDrivers.add(driver);
                }
            }
            if(validAvailableDrivers.isEmpty()) {
                return null;
            }
            return availableDriverNearest(validAvailableDrivers, ride);
        }
    }

    private boolean canDriverRideNextRide(Driver driver, Ride ride) {// ako je voznja pending i ima vozaca tog znaci da ne moze da vozi
        // jer voznje koje imaju vozaca su samo one u bliskoj buducnosti nece imati vozaca voznja koja je za npr sat vremena ili vise od sad
        List<Ride> pendingRides = rideRepository.findAllByDriverAndStatus(driver, RideStatus.PENDING);
        if (!pendingRides.isEmpty()) {
            return false;
        }

        List<Ride> activeRides = rideRepository.findAllByDriverAndStatus(driver, RideStatus.IN_PROGRESS);
        if (activeRides.isEmpty()) {
            return true;
        }

        Ride activeRide = activeRides.get(0);
        LocalDateTime endTimeEstimation = activeRide.getStartTime()
                .plusMinutes(Math.round(activeRide.getEstimatedDuration()));
        Duration leftTime = Duration.between(LocalDateTime.now(), endTimeEstimation);

        return leftTime.toMinutes() <= 10;
    }
private Driver scoring(List<Driver>drivers,Ride ride){
        HashMap<Driver,Double> nearestInTime = nearestTimeToEndDriver(drivers);
        HashMap<Driver,Double> nearestInDistance=closestToStart(drivers,ride);
        HashMap<Driver,Double> score=new HashMap<>();

    for (Driver driver : drivers) {
        double time = nearestInTime.getOrDefault(driver, 10.0);
        double distance = nearestInDistance.getOrDefault(driver, 0.0);

        double totalScore = 0.3 * time + 0.7 * distance / 1000;
        score.put(driver, totalScore);
    }

    return score.entrySet()
            .stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
}
    private HashMap<Driver,Double> nearestTimeToEndDriver(List<Driver> drivers) {
        HashMap<Driver, Double> driverLeftTime = new HashMap<>();
        for(Driver driver:drivers){
            List<Ride> currentRide=rideRepository.findAllByDriverAndStatus(driver,RideStatus.IN_PROGRESS);
            if(currentRide.size()>1){
                System.out.println("VOZAC IMA VISE OD JEDNE VOZNJE AKTIVNE! Greska!");
            }
            Ride rideInProgress=currentRide.get(0);
            LocalDateTime endTimeEstimation=rideInProgress.getStartTime().plusMinutes(Math.round(rideInProgress.getEstimatedDuration()));
            Duration leftTime= Duration.between(LocalDateTime.now(),endTimeEstimation);
            driverLeftTime.put(driver,(double)leftTime.toMinutes());
        }

       return driverLeftTime;

    }

    private HashMap<Driver,Double> closestToStart(List<Driver> drivers,Ride ride){
        HashMap<Driver,Double>  driverLeftMeters=new HashMap<>();
        for (Driver driver:drivers){
            List<Ride> currentRide=rideRepository.findAllByDriverAndStatus(driver,RideStatus.IN_PROGRESS);
            if(currentRide.size()>1){
                System.out.println("VOZAC IMA VISE OD JEDNE VOZNJE AKTIVNE! Greska!");
            }
            Ride rideInProgress=currentRide.get(0);
            driverLeftMeters.put(driver,distanceInMeters(ride.getStartLocation().getLatitude(),ride.getStartLocation().getLongitude()
                    ,rideInProgress.getEndLocation().getLatitude(),rideInProgress.getEndLocation().getLongitude()));

        }
        return driverLeftMeters;
    }
   private double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance;
    }
    private boolean checkDriverActivity(List<DriverSession> sessions,boolean scheduled){
        LocalDateTime nowMinus24h=LocalDateTime.now().minusHours(24);
        LocalDateTime now=LocalDateTime.now();
        Duration duration=Duration.ZERO;
        for(DriverSession session:sessions){
            LocalDateTime start = session.getStartedAt();
            LocalDateTime end = session.getEndedAt() != null ? session.getEndedAt() : now;
            if(end.isBefore(nowMinus24h)) {
                continue;
            }
            if(start.isAfter(now)) {
                continue;
            }
            LocalDateTime effectiveStart = start.isBefore(nowMinus24h) ? nowMinus24h : start;
            LocalDateTime effectiveEnd = end.isAfter(now) ? now : end;
            duration = duration.plus(Duration.between(effectiveStart, effectiveEnd));
        }
        if(!scheduled) {
            return duration.toHours() <= 8;
        }else {
            return duration.toMinutes() <= 7;
        }
    }

    private Driver availableDriverNearest(List<Driver> drivers,Ride ride){
        HashMap<Driver,Double>  driverDistanceMeters=new HashMap<>();
        for(Driver driver:drivers){
            driverDistanceMeters.put(driver,distanceInMeters(driver.getCurrentLocation().getLatitude(),driver.getCurrentLocation().getLongitude(),
                    ride.getStartLocation().getLatitude(),ride.getStartLocation().getLongitude()));
        }
        return driverDistanceMeters.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public void setDriversScheduledRides() {
        List<Ride> pendingRides=rideRepository.findAllByDriverAndStatus(null,RideStatus.PENDING);
        List<Ride> upcominPendingRides=new ArrayList<>();
        for(Ride ride:pendingRides){
           Duration dur= Duration.between(LocalDateTime.now(),ride.getStartTime());
           if(dur.toMinutes()<=30){
               upcominPendingRides.add(ride);
           }
        }
        List<Driver> activeDrivers=driverRepository.findByIsActiveTrue();
        List<Driver> validActiveDrivers = new ArrayList<>();
        for(Driver driver : activeDrivers) {
            if(checkDriverActivity(driverSessionRepo.findByDriver(driver),false)) {
                validActiveDrivers.add(driver);
            }
        }
        for(Ride ride:upcominPendingRides) {
            Driver driver=availableDriverNearest(validActiveDrivers,ride);
            ride.setDriver(driver);
            driver.setAvailable(false);
            driverRepository.save(driver);
            rideRepository.save(ride);
            validActiveDrivers.remove(driver);
        }
    }
}
