package com.example.onlineshoppingsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "user_type", nullable = false)
    private Integer userType; // 1-顾客，2-卖家

    @Column(name = "status")
    private Integer status = 1; // 0-注销，1-正常

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 获取角色字符串
    public String getRole() {
        if (userType == null) {
            return null;
        }
        return userType == 1 ? "customer" : "seller";
    }

    // 设置角色字符串
    public void setRole(String role) {
        if (role == null) {
            this.userType = null;
        } else if ("customer".equalsIgnoreCase(role)) {
            this.userType = 1;
        } else if ("seller".equalsIgnoreCase(role)) {
            this.userType = 2;
        } else {
            throw new IllegalArgumentException("无效的角色值: " + role);
        }
    }
}