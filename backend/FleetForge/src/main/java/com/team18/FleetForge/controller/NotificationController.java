package com.team18.FleetForge.controller;

import com.team18.FleetForge.dto.NotificationDTO;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
}