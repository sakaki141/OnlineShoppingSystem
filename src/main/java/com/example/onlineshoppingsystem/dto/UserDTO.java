package com.example.onlineshoppingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer userId;
    private String username;
    private String phone;
    private String email;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}