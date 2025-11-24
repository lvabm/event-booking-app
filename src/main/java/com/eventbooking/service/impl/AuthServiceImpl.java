package com.eventbooking.service.impl;

import com.eventbooking.dto.auth.AuthResponse;
import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.dto.auth.RegisterRequest;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.entity.User;
import com.eventbooking.exception.UnauthorizedException;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.AuthService;
import com.eventbooking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;

  @Override
  public AuthResponse login(LoginRequest request) {
    // Tìm user theo email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

    // Verify password với BCrypt
    try {
      boolean passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());
      if (!passwordMatches) {
        throw new UnauthorizedException("Invalid email or password");
      }
    } catch (IllegalArgumentException e) {
      // Nếu password hash không hợp lệ (không đúng format BCrypt)
      throw new UnauthorizedException("Invalid email or password");
    } catch (Exception e) {
      // Các lỗi khác
      throw new UnauthorizedException("Invalid email or password");
    }

    // Generate JWT token
    String token = jwtUtil.generateToken(user.getId(), user.getEmail());

    return AuthResponse.builder()
        .token(token)
        .type("Bearer")
        .build();
  }

  @Override
  public UserResponse register(RegisterRequest request) {
    return null;
  }
}
