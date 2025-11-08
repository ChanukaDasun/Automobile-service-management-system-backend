package com.automobilesystem.automobile.Repository;

import com.automobilesystem.automobile.model.ChatMessages;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessagesRepository extends MongoRepository<ChatMessages, String> {
    List<ChatMessages> findByChatId(String s);
}
