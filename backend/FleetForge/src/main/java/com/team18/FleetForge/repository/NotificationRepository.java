package com.team18.FleetForge.repository;

import com.team18.FleetForge.model.Notification;
import com.team18.FleetForge.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndIsReadFalse(User user);

    long countByUserAndIsReadFalse(User user);
}