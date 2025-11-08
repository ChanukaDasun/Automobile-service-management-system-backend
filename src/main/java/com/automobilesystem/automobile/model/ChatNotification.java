package com.automobilesystem.automobile.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private String id;
    private String recipientId;
    private String senderId;
    private String content;
}
