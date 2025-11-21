package com.eventbooking.service;

import com.eventbooking.dto.reminder.ReminderResponse;
import com.eventbooking.dto.reminder.ReminderUpdateRequest;

public interface ReminderService {
  /** Cập nhật tuỳ chọn nhắc nhở sự kiện */
  ReminderResponse updateReminderSettings(ReminderUpdateRequest request);
}
