package com.team18.FleetForge.RideOrderTest.service;

import com.team18.FleetForge.dto.NotificationDTO;
import com.team18.FleetForge.model.Notification;
import com.team18.FleetForge.model.enums.NotificationType;
import com.team18.FleetForge.model.users.Passenger;
import com.team18.FleetForge.repository.NotificationRepository;
import com.team18.FleetForge.service.NotificationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {
    private NotificationRepository notificationRepository;
    private SimpMessagingTemplate messagingTemplate;

    private NotificationService notificationService;

    @BeforeMethod
    public void setUp(){
        notificationRepository= mock(NotificationRepository.class);
        messagingTemplate=mock(SimpMessagingTemplate.class);
        notificationService=new NotificationService(notificationRepository,null,messagingTemplate);
    }

    private Passenger newPassenger(String email) {
        Passenger user = new Passenger();
        user.setEmail(email);
        return user;
    }

    @Test
    public void sendNotificationToUser() {
        Passenger user = newPassenger("test@test.com");
        Notification saved = Notification.builder()
                .user(user)
                .type(NotificationType.RIDE_CREATED)
                .message("Test poruka")
                .isRead(false)
                .build();
        when(notificationRepository.save(any())).thenReturn(saved);

        notificationService.sendNotificationToUser(user, NotificationType.RIDE_CREATED, "Test poruka", null);

        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSendToUser(eq(user.getEmail()),eq("/queue/notifications"),any(NotificationDTO.class));
    }



}
