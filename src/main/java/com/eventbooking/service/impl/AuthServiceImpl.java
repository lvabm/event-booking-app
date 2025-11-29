package com.eventbooking.service.impl;

import com.eventbooking.common.constant.Role;
import com.eventbooking.dto.auth.*;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.entity.User;
import com.eventbooking.exception.ConflictException;
import com.eventbooking.exception.UnauthorizedException;
import com.eventbooking.mapper.UserMapper;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.AuthService;
import com.eventbooking.service.JwtService;
import com.eventbooking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        // 1. XÁC THỰC (Authentication)
        var authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        // 2. LẤY USER VÀ TẠO TOKEN
        var user = (User) authentication.getPrincipal();

        // Tạo JWT Token
        String accessToken = jwtService.generateToken(user);
        // Lấy thời gian hết hạn (ví dụ: 3600 giây)
        Long expiresInSeconds = jwtService.getExpirationTimeInSeconds();

        // 3. TRẢ VỀ LOGIN RESPONSE
        return new LoginResponse(accessToken, expiresInSeconds);
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ConflictException("Email is already registered");
        }

        var registerUser = userMapper.toEntity(registerRequest);
        registerUser.setRole(Role.USER);
        registerUser.setPassword(passwordEncoder.encode(registerRequest.password()));

        return userMapper.toDTO(userRepository.save(registerUser));
    }
}