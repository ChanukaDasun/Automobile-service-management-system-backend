package com.automobilesystem.automobile.Service;

import com.automobilesystem.automobile.Repository.UserRepository;
import com.automobilesystem.automobile.model.User;
import com.automobilesystem.automobile.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean validateUserByNickname(String nickname) {
        boolean result = false;
        var existingUser = userRepository.findUserByNickname(nickname);
        if (existingUser != null) {
            result = true;
        }
        return result;
    }

    public void saveUser(User user){
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(User user){
        var storedUser = userRepository.findById(user.getNickname()).orElse(null);
        if(storedUser != null){
            storedUser.setStatus(UserStatus.OFFLINE);
            userRepository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers(){
        return  userRepository.findAllByStatus(UserStatus.ONLINE);
    }
}
