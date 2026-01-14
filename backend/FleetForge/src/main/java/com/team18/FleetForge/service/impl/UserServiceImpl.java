package com.team18.FleetForge.service.impl;

import com.team18.FleetForge.model.users.Admin;
import com.team18.FleetForge.model.users.Driver;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.repository.UserRepository;
import com.team18.FleetForge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repo;




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
}
