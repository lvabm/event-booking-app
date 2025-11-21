package com.eventbooking.service.impl;

import com.eventbooking.dto.user.UserProfileUpdateRequest;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  @Override
  public UserResponse getCurrentUser() {
    return null;
  }

  @Override
  public UserResponse updateProfile(UserProfileUpdateRequest request) {
    return null;
  }
}
