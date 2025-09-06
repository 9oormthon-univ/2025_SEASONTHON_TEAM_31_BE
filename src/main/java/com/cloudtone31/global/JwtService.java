package com.cloudtone31.global;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKey jwtSecretKey;

    public String issueToken(Long userId) {
        long now = System.currentTimeMillis();
        long expiry = now + (60 * 60 * 1000); // 1시간

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
