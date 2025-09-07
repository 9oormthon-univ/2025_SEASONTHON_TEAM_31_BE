package com.cloudtone31.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/auth/kakao")
@RequiredArgsConstructor
public class KakaoMobileAuthController {

    private final StringRedisTemplate redisTemplate;
    //private final ObjectProvider<StringRedisTemplate> redisTemplateProvider;

//    @GetMapping("/test")
//    public String test() {
//        var redisTemplate = redisTemplateProvider.getIfAvailable();
//        if (redisTemplate == null) {
//            return "Redis disabled on this profile";
//        }
//        redisTemplate.opsForValue().set("foo","bar");
//        return "ok";
//    }

    @GetMapping("/mobile-url")
    public Map<String, String> mobileAuthorizeUrl(HttpServletRequest req) throws UnsupportedEncodingException {
        // 랜덤 state (1~2분 TTL로 유효표시)
        String state = UUID.randomUUID().toString();
        // "이 state는 모바일 플로우다" 표식 저장
        redisTemplate.opsForValue().set("oauth:state:"+state, "mobile", Duration.ofMinutes(5));

        // 리다이렉트 URI와 client-id는 application.yml에 설정돼 있음
        String authorize = UriComponentsBuilder
                .fromUriString("/oauth2/authorization/kakao")
                .queryParam("state", "mobile:"+state) // 모바일 표식
                .build().toUriString();

        // 프록시 환경이면 절대 URL로 만들기
        String base = req.getRequestURL().toString().replace(req.getRequestURI(), "");
        String url = base + authorize;

        return Map.of("authorizeUrl", url);
    }
}
