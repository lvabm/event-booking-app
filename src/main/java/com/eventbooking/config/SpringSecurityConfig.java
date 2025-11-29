package com.eventbooking.config;

import com.eventbooking.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserDetailsService userDetailsService;

    // 1. Cấu hình Filter Chain chính
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF (Bắt buộc cho ứng dụng API Stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // 2. Quản lý Session (Bắt buộc cho JWT)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. Phân quyền Request (Authorization)
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/api/auth/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/events").permitAll()
                                        .anyRequest().authenticated())
                // 4. Cấu hình Provider
                .authenticationProvider(authenticationProvider())
                // 5. Xử lý Ngoại lệ (Authentication/Access Denied)
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(
                                                (req, res, e) -> {
                                                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                    res.setContentType("application/json");
                                                    res.setCharacterEncoding("UTF-8");
                                                    res.getWriter().write(
                                                            "{\"success\":false,\"message\":\"Unauthorized – Please login to access this resource\"}");
                                                })
                                        .accessDeniedHandler(
                                                (req, res, e) -> {
                                                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                    res.setContentType("application/json");
                                                    res.setCharacterEncoding("UTF-8");
                                                    res.getWriter().write("{\"success\":false,\"message\":\"Forbidden: Not enough privileges\"}");
                                                }))
                // 6. Thêm Filter JWT tùy chỉnh vào chuỗi
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. Cấu hình Authentication Provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Cấu hình Password Encoder (Giữ nguyên)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationProvider authenticationProvider) {
        // Dùng ProviderManager để quản lý AuthenticationProvider đã cấu hình
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }
}

