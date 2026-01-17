package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.driver.DriverCreateRequestDTO;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserService {

    Passenger getCurrentPassenger();
    Admin getCurrentAdmin();
    Driver getCurrentDriver();
    void save(User user);
    User getUserById(Long id);
    Driver createDriver(DriverCreateRequestDTO driver);
}
