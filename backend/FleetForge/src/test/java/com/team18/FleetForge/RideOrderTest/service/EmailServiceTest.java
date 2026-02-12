package com.team18.FleetForge.RideOrderTest.service;

import com.team18.FleetForge.model.ride.Ride;
import com.team18.FleetForge.service.EmailService;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class EmailServiceTest {
    @InjectMocks
    @Spy //send email je u istoj klasi p amoram
    private EmailService emailService;

    @BeforeMethod
    private void setup(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void sendRideNotificationEmailwithCorrectSubject() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setStartAddress("Adresa A");
        ride.setEndAddress("Adresa B");

        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        emailService.sendRideNotificationEmail("test@test.com", ride);

        verify(emailService).sendEmail(
                eq("test@test.com"),
                eq("You've been added to a ride"),
                contains("Adresa A")
        );
    }
}
