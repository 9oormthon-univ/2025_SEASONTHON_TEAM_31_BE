package com.cloudtone31.global.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey jwtSecretKey(@Value("${security.jwt.secret}") String raw) {
        // 공백/따옴표 제거
        String secret = raw.trim().replace("\"", "").replace("'", "");

        byte[] keyBytes;
        try {
            // 일반 Base64 우선
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (DecodingException e) {
            // 실패하면 Base64URL 시도
            keyBytes = Decoders.BASE64URL.decode(secret);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}