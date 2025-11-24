package com.eventbooking.controller;

import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.dto.auth.AuthResponse;
import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.service.AuthService;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<BaseResponse<AuthResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    
    AuthResponse authResponse = authService.login(request);

    return ResponseEntity.ok(
        ApiResponseBuilder.success("Login successful", authResponse));
  }
}

