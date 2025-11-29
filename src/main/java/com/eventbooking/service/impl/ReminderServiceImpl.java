package com.eventbooking.service.impl;

import com.eventbooking.dto.reminder.ReminderResponse;
import com.eventbooking.dto.reminder.ReminderUpdateRequest;
import com.eventbooking.entity.Reminder;
import com.eventbooking.entity.User;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.ReminderRepository;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReminderServiceImpl implements ReminderService {
  private final ReminderRepository reminderRepository;
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
  public ReminderResponse updateReminderSettings(ReminderUpdateRequest request){
     User currentUserEntity = getCurrentUserEntity();

     Reminder reminder = reminderRepository.findByUser_Id(currentUserEntity.getId())
             .orElseGet(() -> { Reminder r = new Reminder();
             r.setUser(currentUserEntity);
                     return r;
             });
        reminder.setEventReminder(request.getEventReminder());

        Reminder savedReminder = reminderRepository.save(reminder);

        return new ReminderResponse(savedReminder.getEventReminder());
  }
}
