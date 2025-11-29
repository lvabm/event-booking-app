package com.eventbooking;

import com.eventbooking.dto.reminder.ReminderResponse;
import com.eventbooking.dto.reminder.ReminderUpdateRequest;
import com.eventbooking.dto.user.UserProfileUpdateRequest;
import com.eventbooking.dto.user.UserResponse;
import com.eventbooking.entity.User;
import com.eventbooking.repository.ReminderRepository;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.ReminderService;
import com.eventbooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventBookingAppApplicationTests {

  @Autowired
  private UserService userService;

  @Autowired
  private ReminderService reminderService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReminderRepository reminderRepository;

  @Test
  void contextLoads() {
    User user =
            userRepository
                    .findByEmail("user1@example.com")
                    .orElseThrow(() -> new RuntimeException("Test user not found"));

    // 2. Giả lập user đang đăng nhập bằng SecurityContextHolder
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                    user.getEmail(), null, Collections.emptyList());
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);


    UserProfileUpdateRequest profileRequest = new UserProfileUpdateRequest();
    profileRequest.setFullName("Test Name From JUnit");
    profileRequest.setAvatar("https://example.com/test-avatar.png");

    UserResponse updatedUser = userService.updateProfile(profileRequest);

    assertNotNull(updatedUser);
    assertEquals(user.getId().toString(), updatedUser.getUserId());
    assertEquals("Test Name From JUnit", updatedUser.getFullName());
    assertEquals("https://example.com/test-avatar.png", updatedUser.getAvatar());
    assertEquals("user1@example.com", updatedUser.getEmail());


    ReminderUpdateRequest reminderRequest = new ReminderUpdateRequest();
    reminderRequest.setEventReminder(false); // tắt nhắc nhở

    ReminderResponse reminderResponse = reminderService.updateReminderSettings(reminderRequest);

    assertNotNull(reminderResponse);
    assertFalse(reminderResponse.getEventReminder());

    // Kiểm tra lại trong DB cho chắc
    var reminderOpt = reminderRepository.findByUser_Id(user.getId());
    assertTrue(reminderOpt.isPresent());
    assertFalse(reminderOpt.get().getEventReminder());

    // 3. Dọn SecurityContext sau khi test
    SecurityContextHolder.clearContext();
  }


  @Test
  void updateProfile_shouldThrow_whenUserNotFound() {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication =
            new UsernamePasswordAuthenticationToken(
                    "unknown@example.com", null, Collections.emptyList());
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context);

    UserProfileUpdateRequest req = new UserProfileUpdateRequest();
    req.setFullName("Test");
    req.setAvatar("https://example.com/avatar.png");

    assertThrows(RuntimeException.class,
            () -> userService.updateProfile(req));

    SecurityContextHolder.clearContext();
  }

}
