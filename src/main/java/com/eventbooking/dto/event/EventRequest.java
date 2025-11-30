package com.eventbooking.dto.event;

import com.eventbooking.common.constant.RegexPattern;
import com.eventbooking.util.FutureDateString;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
public record EventRequest (
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Date & Time is required")
        @FutureDateString (regexp = RegexPattern.ISO_DATE)
        String dateTime,

        @NotNull(message = "Location is required")
        String location,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must be >= 0")
        BigDecimal price,

        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,

        @URL(message = "Invalid image URL")
        String imageUrl

) {}
