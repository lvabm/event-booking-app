package com.eventbooking.service.impl;

import com.eventbooking.dto.reminder.ReminderResponse;
import com.eventbooking.dto.reminder.ReminderUpdateRequest;
import com.eventbooking.repository.ReminderRepository;
import com.eventbooking.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReminderServiceImpl implements ReminderService {
  private final ReminderRepository reminderRepository;

  @Override
  public ReminderResponse updateReminderSettings(ReminderUpdateRequest request) {
    return null;
  }
}
