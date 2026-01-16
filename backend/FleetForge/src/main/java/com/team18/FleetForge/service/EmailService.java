package com.team18.FleetForge.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        //todo remove later only for testing
        System.out.println("Sending email to " + to);
        System.out.println("Body: " + body);
//        Email from = new Email(fromEmail);
//        Email toEmail = new Email("ognjenvujovic04@gmail.com"); //todo set "to" instead of hardcoded
//        Content content = new Content("text/plain", body);
//        Mail mail = new Mail(from, subject, toEmail, content);
//
//        SendGrid sg = new SendGrid(apiKey);
//        Request request = new Request();
//
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            sg.api(request);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
    }
}
