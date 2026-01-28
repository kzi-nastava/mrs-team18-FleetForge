package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.dto.driver.DriverCreateRequestDTO;
import com.team18.FleetForge.model.GeoPoint;
import com.team18.FleetForge.model.vecihles.Vehicle;
import com.team18.FleetForge.model.enums.Role;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.users.UserRepository;
import com.team18.FleetForge.service.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Passenger getCurrentPassenger() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (!(user instanceof Passenger)) {
            return null;
        }
        return (Passenger) user;
    }

    @Override
    public Admin getCurrentAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (!(user instanceof Admin)) {
            return null;
        }
        return (Admin) authentication.getPrincipal();
    }

    @Override
    public Driver getCurrentDriver() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if (!(user instanceof Driver)) {
            return null;
        }
        return (Driver) authentication.getPrincipal();
    }

    @Override
    @Transactional
    public void save(User user) {
        repo.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Driver createDriver(DriverCreateRequestDTO driver) {
        Driver driverEntity = new Driver();
        driverEntity.setFirstName(driver.getFirstName());
        driverEntity.setLastName(driver.getLastName());
        driverEntity.setEmail(driver.getEmail());
        driverEntity.setPhoneNumber(driver.getPhoneNumber());
        driverEntity.setAddress(driver.getAddress());
        driverEntity.setAvailable(false);
        driverEntity.setCurrentLocation(new GeoPoint(45.2651,19.8452));
        driverEntity.setBlocked(false);
        driverEntity.setActive(false);
        driverEntity.setActivated(true);
        String temporaryPassword = UUID.randomUUID().toString();
        driverEntity.setPassword(passwordEncoder.encode(temporaryPassword));
        driverEntity.setRole(Role.ROLE_DRIVER);

        Vehicle vehicleEntity = new Vehicle();
        vehicleEntity.setModel(driver.getVehicle().getModel());
        vehicleEntity.setType(driver.getVehicle().getType());
        vehicleEntity.setSpace(driver.getVehicle().getSpace());
        vehicleEntity.setRegistrationNumber(driver.getVehicle().getRegistrationNumber());
        vehicleEntity.setPetFriendly(driver.getVehicle().isPetFriendly());
        vehicleEntity.setBabySeat(driver.getVehicle().isBabySeat());

        driverEntity.setVehicle(vehicleEntity);

        repo.save(driverEntity);
        return  driverEntity;
    }
}
