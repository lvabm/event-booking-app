package com.eventbooking.controller;

import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.dto.reminder.ReminderResponse;
import com.eventbooking.dto.reminder.ReminderUpdateRequest;
import com.eventbooking.dto.user.UserProfileUpdateRequest;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.service.ReminderService;
import com.eventbooking.service.UserService;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final ReminderService reminderService;

  @GetMapping("/profile")
  public ResponseEntity<BaseResponse<UserResponse>> getCurrentUserProfile() {
    // Lấy thông tin User đang đăng nhập từ Service // sẽ đổi lại thành lấy từ userdetail
    UserResponse data = userService.getCurrentUser();

    return ResponseEntity.ok(ApiResponseBuilder.success("Lấy thông tin cá nhân thành công", data));
  }

  @PutMapping("/profile")
  public ResponseEntity<BaseResponse<UserResponse>> updateProfile(
      @Valid @RequestBody UserProfileUpdateRequest request) {

    // Cập nhật thông tin và nhận về UserResponse đã cập nhật
    UserResponse updatedUser = userService.updateProfile(request);

    return ResponseEntity.ok(
        ApiResponseBuilder.success("Hồ sơ cá nhân cập nhật thành công", updatedUser));
  }

  @PutMapping("/reminders")
  public ResponseEntity<BaseResponse<ReminderResponse>> updateReminderSettings(
      @Valid @RequestBody ReminderUpdateRequest request) {

    // Cập nhật cài đặt nhắc nhở
    ReminderResponse updatedSettings = reminderService.updateReminderSettings(request);

    return ResponseEntity.ok(
        ApiResponseBuilder.success("Cài đặt nhắc nhở cập nhật thành công", updatedSettings));
  }
}
