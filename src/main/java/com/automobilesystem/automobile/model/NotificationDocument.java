package com.automobilesystem.automobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationDocument {
    @Id
    private String id;

    // recipient principal name (user id) — null for broadcast
    private String to;

    private String message;

    private Instant createdAt;

    private boolean read;
}
