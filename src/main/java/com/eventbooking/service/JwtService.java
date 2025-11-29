package com.eventbooking.service;

import com.eventbooking.entity.User;

public interface JwtService {

    String generateToken(User user);

    String extractEmail(String token);

    Long getExpirationTimeInSeconds();
}

