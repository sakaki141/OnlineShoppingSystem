package com.example.onlineshoppingsystem.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String phone;
    private String email;
    private String role; // "customer" 或 "seller"
}