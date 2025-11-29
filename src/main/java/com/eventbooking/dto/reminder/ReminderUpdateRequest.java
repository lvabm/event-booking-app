package com.eventbooking.dto.reminder;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReminderUpdateRequest {

    @NotNull(message = "Event reminder preference must not be null")
    private Boolean eventReminder;
}
