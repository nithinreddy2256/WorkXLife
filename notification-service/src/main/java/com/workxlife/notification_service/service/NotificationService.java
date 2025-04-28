package com.workxlife.notification_service.service;

import com.workxlife.notification_service.entity.Notification;
import com.workxlife.notification_service.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;


    public void sendNotification(Notification notification) {
        notification.setTimestamp(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);

        if ("EMAIL".equalsIgnoreCase(notification.getType())) {
            sendEmail(notification);
        }
    }

    private void sendEmail(Notification notification) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject("WorkXLife Notification");
            helper.setText(notification.getMessage(), false);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
