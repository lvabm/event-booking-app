package com.eventbooking.dto.auth;


public record LoginResponse(String accessToken, Long expire) {}
