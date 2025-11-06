package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.model.NotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotifyRestController {

    private final SimpMessagingTemplate template;

    public NotifyRestController(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * Simple HTTP helper to send a notification. Use with Postman while testing frontend connections.
     * Body example: { "to": "user_...", "message": "Hello" }
     */
    @PostMapping("/notify")
    public ResponseEntity<Void> notify(@RequestBody NotificationRequest req) {
        if (req == null) return ResponseEntity.badRequest().build();

        if (req.getTo() != null && !req.getTo().isEmpty()) {
            template.convertAndSendToUser(req.getTo(), "/queue/notifications", req.getMessage());
        } else {
            template.convertAndSend("/topic/notifications", req.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
