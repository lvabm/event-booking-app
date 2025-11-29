package com.eventbooking.service.impl;

import com.eventbooking.dto.user.UserProfileUpdateRequest;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.entity.User;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;


  private User getCurrentUserEntity() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
      throw new IllegalStateException("No authenticated user");
    }
    String email = authentication.getName();
    return userRepository
            .findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }



  @Override
  public UserResponse getCurrentUser(){
    User user = getCurrentUserEntity();
    return toUserResponse(user);
  }

  @Override
  public UserResponse updateProfile(UserProfileUpdateRequest request) {
    User user = getCurrentUserEntity();

    user.setFullName(request.getFullName());
    user.setAvatar(request.getAvatar());
    User save = userRepository.save(user);

    return toUserResponse(save);
  }

    private UserResponse toUserResponse(User user) {
           UserResponse response = new UserResponse();
           response.setUserId(user.getId().toString());
              response.setFullName(user.getFullName());
              response.setEmail(user.getEmail());
                response.setAvatar(user.getAvatar());
              return response;
    }
}
