package com.example.onlineshoppingsystem.config;

import com.example.onlineshoppingsystem.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // 构造函数注入依赖
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Authorization字段
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 提取JWT令牌（格式：Bearer <token>）
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                // 从令牌中解析用户名
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("JWT令牌解析失败: {}");
            }
        }

        // 验证令牌并设置认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证令牌有效性
            if (jwtUtil.validateToken(token, userDetails)) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置认证信息到上下文
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}
