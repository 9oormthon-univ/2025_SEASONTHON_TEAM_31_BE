package com.cloudtone31.global;

import com.cloudtone31.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    // ✅ 이 경로들은 토큰 없이 접근 가능 + 필터 자체를 스킵
    private static final List<String> SKIP_PATTERNS = List.of(
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/",
            "/error",
            "/index.html",
            "/oauth2/**",
            "/login/**",
            "/v1/auth/kakao/**",
            "/v1/auth/token/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // 프리플라이트 요청은 모두 패스
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        // 화이트리스트 경로 패스
        return SKIP_PATTERNS.stream().anyMatch(p -> PATH_MATCHER.match(p, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 토큰이 없으면 그냥 통과 (인증 필요하면 뒷단에서 401 처리)
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // "Bearer " 이후
        try {
            var jws = jwtService.parse(token);
            Claims claims = jws.getBody();

            // typ=access 만 허용
            if (!"access".equals(claims.get("typ"))) {
                chain.doFilter(request, response);
                return;
            }

            String userId = claims.getSubject(); // sub
            var auth = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);

        } catch (JwtException e) {
            // ✅ 예외는 반드시 잡아서 응답을 완결 — ECONNRESET 방지
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"Invalid or expired token\"}"
            );
        }
    }
}