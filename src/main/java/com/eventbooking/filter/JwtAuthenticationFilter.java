package com.eventbooking.filter;

import com.eventbooking.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  // Các endpoint không cần JWT authentication
  private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
      "/api/auth/login",
      "/api/auth/register"
  );

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestPath = request.getRequestURI();

    // Bỏ qua các public endpoints
    if (isPublicEndpoint(requestPath)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Lấy token từ header
    String token = extractTokenFromRequest(request);

    if (token == null) {
      sendUnauthorizedResponse(response, "Missing access token");
      return;
    }

    try {
      // Validate token
      jwtUtil.validateToken(token);
      
      // Lấy thông tin từ token
      Long userId = jwtUtil.getUserIdFromToken(token);
      String email = jwtUtil.getEmailFromToken(token);
      
      // Lưu user ID vào request attribute để sử dụng trong controller/service
      request.setAttribute("userId", userId);
      
      // Tạo Authentication object và set vào SecurityContext
      // Điều này cần thiết để Spring Security nhận biết user đã được authenticate
      UsernamePasswordAuthenticationToken authentication = 
          new UsernamePasswordAuthenticationToken(
              email, 
              null, 
              Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
          );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      
      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      sendUnauthorizedResponse(response, "Token has expired");
    } catch (MalformedJwtException | IllegalArgumentException e) {
      sendUnauthorizedResponse(response, "Invalid token");
    } catch (Exception e) {
      sendUnauthorizedResponse(response, "Unauthorized – Please login to access this resource");
    }
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private boolean isPublicEndpoint(String path) {
    return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    String jsonResponse = String.format(
        "{\"success\":false,\"message\":\"%s\"}",
        message
    );
    
    response.getWriter().write(jsonResponse);
  }
}

