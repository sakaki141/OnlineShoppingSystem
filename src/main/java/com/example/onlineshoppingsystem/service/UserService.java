package com.example.onlineshoppingsystem.service;

import com.example.onlineshoppingsystem.entity.User;
import com.example.onlineshoppingsystem.exception.Result;

public interface UserService {
    Result<User> register(User user);
    Result<User> login(String username, String password);
    User findByUsername(String username);
    User findById(Integer userId);
    Result<String> updateUserInfo(User user);
    Result<String> changePassword(Integer userId, String oldPassword, String newPassword);
    Result<String> logout();
}