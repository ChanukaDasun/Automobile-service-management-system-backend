package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RealTimeNotifiController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RealTimeNotifiController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Accepts a NotificationRequest. If 'to' is provided, sends to that user's queue
     * destination `/user/{to}/queue/notifications`. If 'to' is empty, broadcasts to `/topic/notifications`.
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload NotificationRequest request) {
        if (request == null) return;
        String target = request.getTo();
        String payload = request.getMessage();
        System.out.println("Received notification request: to=" + target + " message=" + payload);
        if (target != null && !target.isEmpty()) {
            // send to a specific user (requires that the user's WebSocket session has a Principal name)
            messagingTemplate.convertAndSendToUser(target, "/queue/notifications", payload);
        } else {
            // broadcast
            messagingTemplate.convertAndSend("/topic/notifications", payload);
        }
    }
}
