package com.eventbooking.config;

import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.exception.UnauthorizedException;
import com.eventbooking.util.ApiResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        Throwable rootCause = (Throwable) request.getAttribute("jakarta.servlet.error.exception");

        BaseResponse<?> baseResponse = null;

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            baseResponse = ApiResponseBuilder.error("Missing access token", null);
        }

        if (rootCause instanceof UnauthorizedException ex) {
            baseResponse = ApiResponseBuilder.error(ex.getMessage(), null);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(response.getOutputStream(), baseResponse);
    }
}
