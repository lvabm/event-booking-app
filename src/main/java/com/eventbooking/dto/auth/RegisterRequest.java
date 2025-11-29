package com.eventbooking.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
        @NotBlank(message = "Password is required")

        @Size(min = 8, message = "Password must be at least 8 characters")
        // (?=.*[a-zA-Z]) : Phải chứa ít nhất 1 chữ cái (hoa hoặc thường)
        // (?=.*[0-9])    : Phải chứa ít nhất 1 chữ số
        // [a-zA-Z0-9]+   : Chỉ cho phép chữ cái và chữ số
        @Pattern(

                regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).*$",
                message = "Password must contain letters and numbers")
        String password) {}

