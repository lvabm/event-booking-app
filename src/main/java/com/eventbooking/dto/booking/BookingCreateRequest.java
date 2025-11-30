package com.eventbooking.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingCreateRequest (
        @Min(message = "Event ID must be greater then 0", value = 0)
        @NotNull(message = "Event is required")
        Long eventId,

        @Min(message = "Quantity must be at least 1", value = 1)
        @NotNull(message = "Event is required")
        Integer quantity
){}
