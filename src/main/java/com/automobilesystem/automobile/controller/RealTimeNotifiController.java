package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.model.NotificationRequest;
import com.automobilesystem.automobile.Service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class RealTimeNotifiController {

    private final NotificationService notificationService;

    public RealTimeNotifiController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Accepts a NotificationRequest. Persist and send it using NotificationService.
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload NotificationRequest request) {
        if (request == null) return;
        notificationService.saveAndSend(request);
    }
}
