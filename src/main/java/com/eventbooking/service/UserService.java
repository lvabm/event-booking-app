package com.eventbooking.service;

import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.dto.user.UserProfileUpdateRequest;

public interface UserService {
  /** Lấy thông tin User đang đăng nhập */
  UserResponse getCurrentUser();

  /** Cập nhật thông tin cá nhân (fullName, avatar) */
  UserResponse updateProfile(UserProfileUpdateRequest request);
}
