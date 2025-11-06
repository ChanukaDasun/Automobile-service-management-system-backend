package com.automobilesystem.automobile.controller;

import com.automobilesystem.automobile.Service.ClerkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin( origins = "*")
public class UserController {

    private final ClerkService clerkService;

    @GetMapping("/allUsers")
    public List<?> getAllUsers() throws Exception {
        return clerkService.getAllUsers();
    }

}
