package com.eventbooking.service.impl;

import com.eventbooking.dto.auth.AuthResponse;
import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.dto.auth.RegisterRequest;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  @Override
  public AuthResponse login(LoginRequest request) {
    return null;
  }

  @Override
  public UserResponse register(RegisterRequest request) {
    return null;
  }
}
