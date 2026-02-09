package com.team18.FleetForge.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.team18.FleetForge.model.ride.Ride;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        System.out.println("Sending email to " + to);
        System.out.println("Body: " + body);
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendRideNotificationEmail(String to, Ride ride) {
        String subject = "You've been added to a ride";

        String deepLink = String.format("uberapp://ride/%d", ride.getId());
        String webLink = "http://localhost:4200/passenger/current-ride";

        String body = String.format(
                "You have been added to a ride.\n\n" +
                        "Ride Details:\n" +
                        "Ride ID: %d\n" +
                        "From: %s\n" +
                        "To: %s\n" +
                        "Track your ride:\n" +
                        "Mobile app: %s\n" +
                        "Web browser: %s\n\n" ,
                ride.getId(),
                ride.getStartAddress(),
                ride.getEndAddress(),
                deepLink,
                webLink
        );

        sendEmail(to, subject, body);
    }
    
}
