package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Repository.ChatMessagesRepository;
import com.automobilesystem.automobile.model.ChatMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessagesService {
    private final ChatMessagesRepository chatMessagesRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessages save(ChatMessages chatMessages) {
        var chatId = chatRoomService.getChatRoomId(chatMessages.getSenderId(), chatMessages.getRecipientId(), true).orElseThrow();
        chatMessages.setChatId(chatId);
        chatMessagesRepository.save(chatMessages);
        return chatMessages;
    }

    public List<ChatMessages> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(chatMessagesRepository::findByChatId).orElse(new ArrayList<>());
    }
}
