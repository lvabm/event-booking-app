package com.eventbooking.security;

import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.JwtService;
import com.eventbooking.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 1. Lấy Header Authorization
    String authHeader = request.getHeader("Authorization");

    // 2. Kiểm tra và Bỏ qua nếu không có Bearer Token
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Trích xuất Token (Cắt bỏ "Bearer ")
    String token = authHeader.substring(7);

    try {
      // 4. Trích xuất Email từ Token
      String email = jwtService.extractEmail(token);

      // 5. Kiểm tra Username và Context hiện tại
      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // 6. Tải thông tin User từ Database
        // userRepository là UserRepository hoặc UserService đã được inject
        var user = customUserDetailsService.loadUserByUsername(email);

        if (user != null) {
          // Giả định JWT Service đã kiểm tra token hợp lệ trước đó (signature, expiry)

          // 7. Tạo đối tượng Xác thực (Authentication)
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  user, // Principal: đối tượng User (UserDetails)
                  null, // Credentials: luôn là null vì đã xác thực qua token
                  user.getAuthorities() // Authorities: quyền hạn của User
                  );

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // 8. Thiết lập Context Bảo mật
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }

    } catch (Exception e) {
    }

    // 9. Tiếp tục chuỗi Filter
    filterChain.doFilter(request, response);
  }
}
