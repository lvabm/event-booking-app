package com.eventbooking.service;

import com.eventbooking.dto.auth.AuthResponse;
import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.dto.auth.RegisterRequest;
import com.eventbooking.dto.user.UserResponse;

public interface AuthService {
  /** Xác thực người dùng và trả về JWT Token */
  AuthResponse login(LoginRequest request);

  /** Tạo tài khoản mới, kiểm tra email trùng lặp */
  UserResponse register(RegisterRequest request);
}
