package com.team18.FleetForge.service;

import com.team18.FleetForge.dto.NotificationDTO;
import com.team18.FleetForge.model.Notification;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.model.users.User;
import com.team18.FleetForge.model.enums.NotificationType;
import com.team18.FleetForge.repository.NotificationRepository;
import com.team18.FleetForge.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public void sendNotificationToUser(User user, NotificationType type, String message, Ride ride) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .ride(ride)
                .build();

        notification = notificationRepository.save(notification);

        NotificationDTO dto = buildNotificationDTO(notification);
        messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                "/queue/notifications",
                dto
        );

        log.info("Notification sent to user {}: {}", user.getEmail(), message);
    }
    
    @Transactional
    public void sendNotificationToLinkedPassengers(Ride ride, NotificationType type, String message) {
        sendNotificationToUser(ride.getPassenger(), type, message, ride);
        
        if (ride.getLinkedPassengers() != null && !ride.getLinkedPassengers().isEmpty()) {
            for (Passenger passenger : ride.getLinkedPassengers()) {
                sendNotificationToUser(passenger, type, message, ride);
            }
        }
    }
    
    public List<NotificationDTO> getUserNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(this::buildNotificationDTO)
                .collect(Collectors.toList());
    }
    
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    private NotificationDTO buildNotificationDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .rideId(notification.getRide() != null ? notification.getRide().getId() : null)
                .build();
    }
}