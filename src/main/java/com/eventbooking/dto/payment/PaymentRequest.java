package com.eventbooking.dto.payment;

import com.eventbooking.common.constant.RegexPattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequest (
        @NotNull(message = "Booking ID is required")
        @Min(value = 1, message = "Booking ID must be greater than 0")
        Long bookingId,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = RegexPattern.CARD_NUMBER, message = "Invalid card number format")
        String cardNumber,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = RegexPattern.CVV, message = "Invalid CVV format")
        String cvv,

        @NotBlank(message = " Expiry date is required")
        @Pattern(regexp = RegexPattern.CARD_EXPIRER, message = "Invalid expiry format")
        String expiry

){}
