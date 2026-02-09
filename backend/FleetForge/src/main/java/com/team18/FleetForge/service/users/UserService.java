package com.team18.FleetForge.service.users;

import com.team18.FleetForge.dto.driver.DriverCreateRequestDTO;
import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;

import java.util.List;

public interface UserService {

    Passenger getCurrentPassenger();
    Admin getCurrentAdmin();
    Driver getCurrentDriver();
    void save(User user);
    User getUserById(Long id);
    Driver createDriver(DriverCreateRequestDTO driver);

    List<String> searchUserEmailsByPrefix(String prefix);
}
