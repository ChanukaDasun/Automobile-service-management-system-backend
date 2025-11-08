package com.automobilesystem.automobile.Repository;

import com.automobilesystem.automobile.model.User;
import com.automobilesystem.automobile.model.UserStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User,String> {
    List<User> findAllByStatus(UserStatus status);
    User findUserByNickname(String nickname);
}
