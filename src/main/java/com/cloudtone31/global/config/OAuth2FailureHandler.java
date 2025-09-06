package com.cloudtone31.global.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    // 프론트 진입점 (원한다면 /login 으로)
    private static final String FRONT_LOGIN = "http://localhost:3000/login";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException ex) throws IOException {

        String msg = ex.getMessage() == null ? "login_failed" : ex.getMessage();
        log.warn("OAuth2 failure: {}", msg);

        // 1) 세션/상태 유실 케이스 → 자동으로 카카오 로그인 재시작
        if (msg.contains("authorization_request_not_found")) {
            response.sendRedirect(request.getContextPath() + "/oauth2/authorization/kakao");
            return;
        }

        // 2) API 요청(모바일/SPA XHR)에는 JSON으로 응답
        if (isApiRequest(request)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"" + escape(msg) + "\"}");
            return;
        }

        // 3) 일반 브라우저 접근이면 프론트 로그인 화면으로
        String url = FRONT_LOGIN + "?error=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
        response.sendRedirect(url);
    }

    private boolean isApiRequest(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        String xhr = req.getHeader("X-Requested-With");
        String uri = req.getRequestURI();
        return (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xhr)
                || uri.startsWith("/api")
                || uri.startsWith("/v1");
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}