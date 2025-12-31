package com.example.onlineshoppingsystem.controller;

import com.example.onlineshoppingsystem.dto.UserDTO;
import com.example.onlineshoppingsystem.exception.BusinessException;
import com.example.onlineshoppingsystem.exception.Result;
import com.example.onlineshoppingsystem.entity.User;
import com.example.onlineshoppingsystem.service.UserService;
import com.example.onlineshoppingsystem.util.JwtUtil;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            User user = userService.findByUsername(loginRequest.getUsername());
            if (!user.getRole().equals(loginRequest.getRole())) {
                throw new BusinessException("角色不匹配");
            }

            UserDetails userDetails= userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            return Result.success(new LoginResponse(user.getUsername(), token));
        } catch (Exception e) {
            return Result.error("用户名或密码或角色错误: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            // 实体转DTO，过滤敏感字段和关联关系
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO, "password", "participant", "judge", "organizer");
            return success(userDTO);
        } catch (Exception e) {
            return error("获取用户信息失败：" + e.getMessage());
        }
    }


    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        try {
            SecurityContextHolder.clearContext();
            return success("登出成功");
        } catch (Exception e) {
            return error("登出失败: " + e.getMessage());
        }
    }

    // 登录请求参数类
    @Setter
    @Getter
    public static class LoginRequest {
        private String username;
        private String password;
        private String role;
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private String username;
        private String token;

        // 添加构造函数，接收用户名和token
        public LoginResponse(String username, String token) {
            this.username = username;
            this.token = token;
        }
    }
}
