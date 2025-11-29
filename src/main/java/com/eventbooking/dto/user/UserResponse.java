package com.eventbooking.dto.user;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String userId;
    private String fullName;
    private String email;
    private String avatar;
}
