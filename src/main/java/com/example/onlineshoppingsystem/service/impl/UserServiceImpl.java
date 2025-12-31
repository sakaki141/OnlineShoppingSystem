package com.example.onlineshoppingsystem.service.impl;

import com.example.onlineshoppingsystem.entity.User;
import com.example.onlineshoppingsystem.repository.UserRepository;
import com.example.onlineshoppingsystem.service.UserService;
import com.example.onlineshoppingsystem.util.JwtUtil;
import com.example.onlineshoppingsystem.exception.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result<User> register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            return Result.error("用户名已存在");
        }

        // 检查手机号是否已存在
        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            return Result.error("手机号已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            return Result.error("邮箱已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认状态
        user.setStatus(1); // 正常状态
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return Result.success(savedUser);
    }

    @Override
    public Result<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                if (user.getStatus() != null && user.getStatus() == 0) {
                    return Result.error("账号已注销");
                }
                return Result.success(user);
            } else {
                return Result.error("密码错误");
            }
        } else {
            return Result.error("用户不存在");
        }
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElse(null);
    }

    @Override
    public User findById(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.orElse(null);
    }

    @Override
    public Result<String> updateUserInfo(User user) {
        Optional<User> existingUserOpt = userRepository.findById(user.getUserId());
        if (existingUserOpt.isEmpty()) {
            return Result.error("用户不存在");
        }

        User existingUser = existingUserOpt.get();

        // 检查手机号是否被其他用户使用
        if (user.getPhone() != null && 
            !existingUser.getPhone().equals(user.getPhone()) && 
            userRepository.existsByPhone(user.getPhone())) {
            return Result.error("手机号已存在");
        }

        // 检查邮箱是否被其他用户使用
        if (user.getEmail() != null && 
            !existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            return Result.error("邮箱已存在");
        }

        // 更新用户信息
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        
        // 设置更新时间
        existingUser.setUpdateTime(LocalDateTime.now());

        userRepository.save(existingUser);
        return Result.success("用户信息更新成功");
    }

    @Override
    public Result<String> changePassword(Integer userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Result.error("用户不存在");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return Result.success("密码修改成功");
    }

    @Override
    public Result<String> logout() {
        // JWT是无状态的，服务器端不需要特殊处理
        return Result.success("退出登录成功");
    }
}