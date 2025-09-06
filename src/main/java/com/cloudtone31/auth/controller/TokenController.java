package com.cloudtone31.auth.controller;

import com.cloudtone31.auth.service.JwtService;
import com.cloudtone31.auth.service.OneTimeCodeService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/v1/auth/token")
@RequiredArgsConstructor
public class TokenController {

    private final OneTimeCodeService codeService;
    private final JwtService jwtService;

    // 모바일/딥링크 기반 교환
    // Expo(모바일) 전용 1회용 코드 교환
    @PostMapping("/exchange")
    public ResponseEntity<?> exchangeByCode(@RequestBody Map<String,String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error","missing_code"));
        }

        String userId = codeService.consume(code);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error","invalid_or_expired_code"));
        }

        String access = jwtService.issueAccess(userId);
        String refresh = jwtService.issueRefresh(userId);
        return ResponseEntity.ok(Map.of(
                "tokenType","Bearer",
                "accessToken", access,
                "refreshToken", refresh,
                "expiresIn", 60*60
        ));
    }

    // Refresh 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refresh = body.get("refreshToken");
        if (refresh == null) return ResponseEntity.badRequest().build();

        try {
            var claims = jwtService.parse(refresh);
            if (!"refresh".equals(claims.getBody().get("typ"))) {
                return ResponseEntity.status(401).body(Map.of("error", "invalid_token_type"));
            }
            String userId = claims.getBody().getSubject();
            String newAccess = jwtService.issueAccess(userId);
            return ResponseEntity.ok(Map.of(
                    "tokenType","Bearer",
                    "accessToken", newAccess,
                    "expiresIn", 60 * 60
            ));
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(Map.of("error","invalid_or_expired_refresh"));
        }
    }
}