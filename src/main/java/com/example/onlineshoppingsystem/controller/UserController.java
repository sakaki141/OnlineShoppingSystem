package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.dto.UserLoginDTO;
import com.example.onlineshoppingsystem.dto.UserRegisterDTO;
import com.example.onlineshoppingsystem.entity.User;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody UserRegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setRole(registerDTO.getRole());
        
        return userService.register(user);
    }

    @GetMapping("/{userId}")
    public Result<User> getUserById(@PathVariable Integer userId) {
        User user = userService.findById(userId);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    @PutMapping("/password")
    public Result<String> changePassword(@RequestParam Integer userId, 
                                         @RequestParam String oldPassword, 
                                         @RequestParam String newPassword) {
        return userService.changePassword(userId, oldPassword, newPassword);
    }
}