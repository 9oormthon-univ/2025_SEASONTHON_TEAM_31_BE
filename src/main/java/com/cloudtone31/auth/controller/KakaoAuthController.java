package com.cloudtone31.auth.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth/kakao")
public class KakaoAuthController {

    // GET https://api.growme.com/v1/auth/kakao/url
    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getKakaoLoginUrl(HttpServletRequest request) {
        // 현재 요청의 스킴/호스트/포트를 기준으로 절대경로 생성 (로컬/개발/운영 모두 대응)
        String base = ServletUriComponentsBuilder.fromRequest(request)
                .replacePath(null)
                .replaceQuery(null)
                .build()
                .toUriString();

        // 스프링 시큐리티가 제공하는 OAuth2 진입점
        String loginStartUrl = UriComponentsBuilder.fromHttpUrl(base)
                .path("/oauth2/authorization/kakao")
                .toUriString();

        return ResponseEntity.ok(Map.of("url", loginStartUrl));
    }

    // (선택) 바로 리다이렉트 시키고 싶다면 사용: GET /v1/auth/kakao/redirect
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectToKakao() {
        return ResponseEntity.status(302)
                .header("Location", "/oauth2/authorization/kakao")
                .build();
    }
}