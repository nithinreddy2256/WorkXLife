package com.workxlife.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workxlife.notification_service.config.RabbitMQConfig;
import com.workxlife.notification_service.entity.Notification;
import com.workxlife.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


@Component
@RequiredArgsConstructor
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(String notificationJson) {
        try {
            Notification notification = objectMapper.readValue(notificationJson, Notification.class);
            notificationService.sendNotification(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
