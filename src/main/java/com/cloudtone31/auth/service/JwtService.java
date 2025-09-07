package com.cloudtone31.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

// JwtService.java
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.secret}")
    private String secret; // 충분히 긴 랜덤 값
    @Value("${security.jwt.access-minutes:60}")
    private long accessMinutes;
    @Value("${security.jwt.refresh-days:30}")
    private long refreshDays;

    private Key key() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String issueAccess(String userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofMinutes(accessMinutes))))
                .claim("typ", "access")
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String issueRefresh(String userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofDays(refreshDays))))
                .claim("typ", "refresh")
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }
}