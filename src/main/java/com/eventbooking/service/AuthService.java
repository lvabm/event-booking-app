package com.eventbooking.service;

import com.eventbooking.dto.auth.LoginResponse;
import com.eventbooking.dto.auth.RegisterResponse;
import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.dto.auth.RegisterRequest;

public interface AuthService {
  LoginResponse login(LoginRequest request);

  RegisterResponse register(RegisterRequest request);
}
