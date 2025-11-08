package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.Repository.ChatMessagesRepository;
import com.automobilesystem.automobile.Service.ChatMessagesService;
import com.automobilesystem.automobile.model.ChatMessage;
import com.automobilesystem.automobile.model.ChatMessages;
import com.automobilesystem.automobile.model.ChatNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessagesService chatMessagesService;


    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessages chatMessage) {
        ChatMessages savedMsg = chatMessagesService.save(chatMessage);
        // john/queue/messages -> subscribe to the message queue
        simpMessagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages", ChatNotification.builder().id(savedMsg.getId()).senderId(savedMsg.getSenderId()).recipientId(savedMsg.getRecipientId()).content(savedMsg.getContent()).build());
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessages>> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessagesService.findChatMessages(senderId, recipientId));
    }
}