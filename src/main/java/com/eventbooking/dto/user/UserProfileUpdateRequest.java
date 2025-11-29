package com.eventbooking.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @Size(max = 255, message = "Avatar URL is too long")
    @Pattern(
            regexp = "^(https?://).*$",
            message = "Invalid avatar URL",
            groups = {}
    )
    private String avatar;
}
