package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Repository.chatRoomRepository;
import com.automobilesystem.automobile.chatRoom.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final chatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);
        ChatRoom sendRecipient = ChatRoom.builder().chatId(chatId).senderId(senderId).recipientId(recipientId).build();
        ChatRoom RecipientSender = ChatRoom.builder().chatId(chatId).senderId(recipientId).recipientId(senderId).build();
        chatRoomRepository.save(sendRecipient);
        chatRoomRepository.save(RecipientSender);
        return chatId;
    }
}
