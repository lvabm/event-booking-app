package com.eventbooking.service;

import com.eventbooking.dto.auth.*;
import com.eventbooking.dto.user.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);
}
