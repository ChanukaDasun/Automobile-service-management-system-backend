package com.automobilesystem.automobile.service;

import com.automobilesystem.automobile.model.NotificationDocument;
import com.automobilesystem.automobile.model.NotificationRequest;
import com.automobilesystem.automobile.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate template;

    public NotificationService(NotificationRepository repository, SimpMessagingTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    /**
     * Save notification to DB and send it to target user or broadcast.
     */
    public NotificationDocument saveAndSend(NotificationRequest req) {
        NotificationDocument doc = new NotificationDocument();
        doc.setTo(req.getTo());
        doc.setMessage(req.getMessage());
        doc.setCreatedAt(Instant.now());
        doc.setRead(false);

        NotificationDocument saved = repository.save(doc);

        if (req.getTo() != null && !req.getTo().isEmpty()) {
            template.convertAndSendToUser(req.getTo(), "/queue/notifications", req.getMessage());
        } else {
            template.convertAndSend("/topic/notifications", req.getMessage());
        }

        return saved;
    }

    /**
     * Retrieve notifications for a specific user (most recent first).
     */
    public java.util.List<NotificationDocument> findByUser(String user) {
        if (user == null) return java.util.Collections.emptyList();
        return repository.findByToOrderByCreatedAtDesc(user);
    }

}
