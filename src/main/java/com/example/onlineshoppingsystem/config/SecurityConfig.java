package com.example.onlineshoppingsystem.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // 启用Spring Security的Web安全支持
public class SecurityConfig {

    private final UserDetailsService userDetailsService; // 注入自定义的用户详情服务

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // 构造函数注入UserDetailsService（即UserDetailsServiceImpl）
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 暴露AuthenticationManager的Bean，解决自动装配问题
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置HTTP安全规则
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 启用CORS配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 关闭CSRF（前后端分离场景常用）
                .csrf(csrf -> csrf.disable())
                // 设置会话管理为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置URL访问权限
                .authorizeHttpRequests(auth -> auth
                        // 基本页面放行
                        .requestMatchers("/", "/index.html","/register.html").permitAll()
                        // 放行登录、注册等公开接口
                        .requestMatchers("/api/auth/login", "/api/user/register").permitAll()
                        // 静态资源放行
                        .requestMatchers("/customer.html", "/seller.html","/favicon.ico").permitAll()

                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:*");
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Refresh-Token");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
