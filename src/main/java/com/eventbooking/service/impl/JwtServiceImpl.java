package com.eventbooking.service.impl;

import com.eventbooking.entity.User;
import com.eventbooking.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey key;
    private final long expMinutes;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.exp-minutes:60}") long expMinutes) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expMinutes = expMinutes;
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now(); // Thời điểm hiện tại

        return Jwts.builder()
                // Thiết lập Subject (chủ thể) là email
                .subject(user.getEmail())
                // Thêm custom claim (thông tin) về role
                .claim("role", user.getRole().name())
                // Thời điểm tạo token
                .issuedAt(Date.from(now))
                // Thời điểm hết hạn (thời điểm tạo + expMinutes * 60 giây)
                .expiration(Date.from(now.plusSeconds(expMinutes * 60)))
                // Ký token bằng SecretKey
                .signWith(key)
                .compact(); // Nén thành chuỗi JWT
    }

    @Override
    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @Override
    public Long getExpirationTimeInSeconds() {
        return expMinutes * 60L;
    }
}