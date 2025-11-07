package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.Service.ClerkService;
import com.automobilesystem.automobile.Service.UserService;
import com.automobilesystem.automobile.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin( origins = "*")
public class UserController {

    private final ClerkService clerkService;
    private final UserService userService;

    @GetMapping("/allUsers")
    public List<?> getAllUsers() throws Exception {
        return clerkService.getAllUsers();
    }

    @MessageMapping("/user.addUser")
    @SendTo("/user/userNotify") // send some notifications to it
    public User addUser(@Payload User user) throws Exception {
        if (userService.validateUserByNickname(user.getNickname())) {
            return null;
        }
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/userNotify") // send some notifications to it
    public User disconnectUser(@Payload User user) throws Exception {
        userService.disconnect(user);
        return user;
    }

    @GetMapping("/connectedUsers")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

}
