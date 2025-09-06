package com.cloudtone31.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;


// OAuth2SuccessHandler.java
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final StringRedisTemplate redisTemplate;

    // 앱이 등록한 딥링크 스킴(예)
    private static final String MOBILE_DEEPLINK = "myapp://oauth2/callback";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication authentication) throws IOException {
        String state = req.getParameter("state"); // "mobile:<uuid>" 또는 null
        boolean isMobile = false;
        String stateKey = null;

        if (state != null && state.startsWith("mobile:")) {
            String raw = state.substring("mobile:".length());
            stateKey = "oauth:state:"+raw;
            String marker = redisTemplate.opsForValue().get(stateKey);
            isMobile = "mobile".equals(marker);
        }

        // 사용자 식별(예: Kakao id 기반으로 userId 찾기)
        String userId = authentication.getName(); // 필요시 커스텀 Principal에서 추출

        if (isMobile) {
            // 일회용 로그인 코드(1분 TTL, 1회용)
            String loginCode = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("login:code:"+loginCode, userId, Duration.ofMinutes(1));
            if (stateKey != null) redisTemplate.delete(stateKey);

            String deepLink = UriComponentsBuilder.fromUriString(MOBILE_DEEPLINK)
                    .queryParam("code", loginCode)
                    .build().toString();

            res.sendRedirect(deepLink);
            return;
        }

        // 웹 기본 플로우
        res.sendRedirect(req.getContextPath() + "/users/me");
    }
}